package com.jadenyangca.exercise.service;

import com.jadenyangca.exercise.component.AppConfig;
import com.jadenyangca.exercise.component.Occurrence;
import com.jadenyangca.exercise.component.Result;
import com.jadenyangca.exercise.exception.AppException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.lang.reflect.Method;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Jaden
 * @create 2018-09-25
 */
@Component
public class FindingService {
    @Autowired
    AppConfig mulConfig;

    private MappedByteBuffer buffer;
    private RandomAccessFile raf;

    Logger logger = LoggerFactory.getLogger(getClass());

    public void findText(String keyWords, Result result) {
        String filePath = mulConfig.getFilePath();
        try {
            raf = new RandomAccessFile(filePath, "r");
            buffer = raf.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, raf.length());
            List<Occurrence> occurrences = getOccurrences(keyWords);
            result.setOccurrences(occurrences);
            result.setNumber_of_occurrences(occurrences.size());
        } catch (Exception e) {
            logger.error("error happened when solving file on server: ", e);
            throw new AppException("error happened when solving file on server");
        }
    }

    /**
     * get Occurrence list
     * @param keyWords query text
     * @return Occurrence list
     * @throws Exception
     */
    public List<Occurrence> getOccurrences(String keyWords) throws Exception {
        int[] lines = getLinesStartAndEndPosArr(0, buffer.capacity() - 1);
        List<Occurrence> occurrences = new ArrayList<Occurrence>();
        String regex = keyWords.replaceAll("\\.", "\\\\.").replaceAll("\\*", "\\\\*").replaceAll("\\?", "\\\\?");
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        for (int lineNum = 0; lineNum < lines.length / 2; lineNum++) {
            int lineStartIndex = lines[lineNum << 1];
            int lineEndIndex = lines[(lineNum << 1) + 1];
            String lineStr = getContent(lineStartIndex, lineEndIndex);
            Matcher m = pattern.matcher(lineStr);
            while (m.find()) {
                Occurrence occurrence = new Occurrence();
                occurrence.setLine(lineNum);
                occurrence.setStart(m.start() + 1);
                occurrence.setEnd(m.end() + 1);
                occurrences.add(occurrence);
            }
        }

        for (Occurrence occurrence : occurrences) {
            String sentence = getSentence(keyWords, occurrence.getLine(), occurrence.getStart(), occurrence.getEnd());
            occurrence.setIn_sentence(sentence.replaceAll("\r\n", " ").replaceAll("\n", ""));
            occurrence.setLine(occurrence.getLine() + 1);
        }
        close();
        return occurrences;
    }

    /**
     *
     * @param keyWords query text
     * @param lines index lines
     * @param lineNo line number
     * @param startIndex start position of matched string(char)
     * @param endIndex end position of matched string(char)
     * @return sentence the sentence that query text exist in
     * @throws Exception
     */
    private String getSentence(String keyWords, int lineNo, int startIndex, int endIndex) throws Exception {
        int[] lines = getLinesStartAndEndPosArr(0, buffer.capacity() - 1);
        int endIndexByte = getEndIndex(keyWords, lines, lineNo, startIndex, endIndex);
        int startIndexByte = getStartIndex(keyWords, lines, lineNo, startIndex, endIndex);
        String sentence = getContent(startIndexByte, endIndexByte);
        return sentence;
    }

    /**
     * use int arrary to store pointers of start index and end index of lines：
     * line number*2 = end index：line number*2+1 .
     * e.g. lines[0]—>line1 start index, lines[1]->line2 end index
     * @param start start index
     * @param end end index
     * @return lines index array
     * @throws Exception
     */
    private int[] getLinesStartAndEndPosArr(int start, int end) throws Exception {
        // Initial size is 10M
        int[] lines = new int[10240000];
        int lineStartIndex = start;
        int lineNum = 0;
        for (int offset = start; offset <= end; offset++) {
            int c = buffer.get(offset);
            if (c == '\n' || (c == '\r' && buffer.get(offset + 1) != '\n')) {
                lines = writeLinesStartEndPos(lines, lineNum, lineStartIndex, offset);
                lineStartIndex = offset + 1;
                lineNum++;
            }
        }
        if (lineStartIndex <= end) {
            lines = writeLinesStartEndPos(lines, lineNum, lineStartIndex, end);
            lineNum++;
        }
        lines = Arrays.copyOf(lines, lineNum << 1);
        return lines;
    }

    /**
     * store the start index and end index (bytes) of lines in array
     * @param lines lines array
     * @param lineNum line number
     * @param startPos start index
     * @param endPos end index
     */
    private int[] writeLinesStartEndPos(int[] lines, int lineNum, int startPos, int endPos) {
        // shift calculation
        int startIndex = lineNum << 1;
        int endIndex = (lineNum << 1) + 1;
        if (endIndex >= lines.length) {
            lines = ensureCapacity(lines);
        }
        lines[startIndex] = startPos;
        lines[endIndex] = endPos;
        return lines;
    }

    /**
     * get the start position of sentence
     * @param keyWords query text
     * @param lines index lines
     * @param lineNo line number
     * @param startIndex start position of matched string(char)
     * @param endIndex end position of matched string(char)
     * @return start position of sentence(byte)
     * @throws Exception
     */
    private int getStartIndex(String keyWords, int[] lines, int lineNo, int startIndex, int endIndex) throws Exception {
        int tmpStartIndex = startIndex;
        for (int lineNum = lineNo; lineNum > 0; lineNum--) {
            int lineStartIndex = lines[lineNum << 1];
            int lineEndIndex = lines[(lineNum << 1) + 1];

            if(lineNum == lineNo) {
                tmpStartIndex = startIndex;
            }else {
                tmpStartIndex = getContent(lineStartIndex, lineEndIndex).length();
            }

            String line = getContent(lineStartIndex, lineEndIndex);
            if(keyWords.startsWith(".") || keyWords.startsWith("?") || keyWords.startsWith("!") ) {
                startIndex = line.substring(0, tmpStartIndex + 1).getBytes("UTF-8").length;
                return lineStartIndex + startIndex;
            }

            while (tmpStartIndex > 0) {
                char c = line.charAt(tmpStartIndex - 1);
                if (c == '.' || c == '?' || c == '!' || c == '\"') {
                    startIndex = line.substring(0, tmpStartIndex + 1).getBytes("UTF-8").length;
                    return lineStartIndex + startIndex;
                }
                tmpStartIndex--;
            }

        }
        return 0;
    }

    /**
     * get the end position of sentence
     * @param keyWords query text
     * @param lines index lines
     * @param lineNo line number
     * @param startIndex start position of matched string(char)
     * @param endIndex end position of matched string(char)
     * @return end position of sentence(byte)
     * @throws Exception
     */
    private int getEndIndex(String keyWords, int[] lines, int lineNo, int startIndex, int endIndex) throws Exception {
        int tmpEndIndex;

        for (int lineNum = lineNo; lineNum < lines.length / 2; lineNum++) {

            int lineStartIndex = lines[lineNum << 1];
            int lineEndIndex = lines[(lineNum << 1) + 1];

            if(lineNum == lineNo) {
                tmpEndIndex = endIndex;
            }else {
                tmpEndIndex = 0;
            }

            String line = getContent(lineStartIndex, lineEndIndex);
            if(keyWords.endsWith(".") || keyWords.endsWith("?") || keyWords.endsWith("!") ) {
                endIndex = line.substring(0, tmpEndIndex).getBytes("UTF-8").length;
                return lineStartIndex + endIndex;
            }
            while (tmpEndIndex < line.length()) {
                char c = line.charAt(tmpEndIndex);
                if (c == '.' || c == '?' || c == '!') {
                    endIndex = line.substring(0, tmpEndIndex).getBytes("UTF-8").length;
                    return lineStartIndex + endIndex;
                }
                tmpEndIndex++;
            }

        }
        return buffer.capacity() - 1;
    }

    /**
     * get the string content from memory mapping
     * @param start start index(byte)
     * @param end end index(byte)
     * @return string content
     * @throws UnsupportedEncodingException
     */
    public String getContent(int start, int end) throws UnsupportedEncodingException {
        byte[] lineBytes = new byte[end - start + 1];
        for (int i = start; i <= end; i++) {
            lineBytes[i - start] = buffer.get(i);
        }
        return new String(lineBytes, "UTF-8");
    }

    /**
     * expand the Arrary
     * @param oldArray
     * @return new expanded Arrary
     */
    private int[] ensureCapacity(int[] oldArray) {
        int oldCapacity = oldArray.length;
        int newCapacity = oldCapacity + (oldCapacity >> 1);
        return Arrays.copyOf(oldArray, newCapacity);
    }

    /**
     * close the resources
     * @throws Exception catchable Exceptions
     */
    private void close() throws Exception {
        Method getCleanerMethod = buffer.getClass().getMethod("cleaner", new Class[0]);
        getCleanerMethod.setAccessible(true);
        sun.misc.Cleaner cleaner = (sun.misc.Cleaner) getCleanerMethod.invoke(buffer, new Object[0]);
        cleaner.clean();
        raf.close();
    }
}
