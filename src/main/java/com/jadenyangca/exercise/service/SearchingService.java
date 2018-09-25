package com.jadenyangca.exercise.service;

import com.jadenyangca.exercise.exception.AppException;
import com.jadenyangca.exercise.component.AppConfig;
import com.jadenyangca.exercise.component.Occurrence;
import com.jadenyangca.exercise.component.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Jaden
 * @create 2018-09-24
 */
@Component
public class SearchingService {

    @Autowired
    AppConfig mulConfig;

    Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * find the locations of key words in the text file
     * @param keyWords query_text
     * @param result return value for users
     */
    public void searchText(String keyWords, Result result) {

        String filePath = mulConfig.getFilePath();
        FileInputStream inputStream = null;
        Scanner sc = null;
        try {
            inputStream = new FileInputStream(filePath);

            //to avoid the OutOfMemory error, the memory will be freed, so it can read the large file
            sc = new Scanner(inputStream, "UTF-8");
            long lineNo = 0;
            List<Occurrence> occurrences = new ArrayList<Occurrence>();
            String regex = keyWords.replaceAll("\\.","\\\\.")
                    .replaceAll("\\*","\\\\*")
                    .replaceAll("\\?","\\\\?");
            Pattern pattern = Pattern.compile(regex,Pattern.CASE_INSENSITIVE);
            while (sc.hasNextLine()) {
                lineNo++;
                String line = sc.nextLine();
                Matcher m = pattern.matcher(line);
                while (m.find()) {
                    Occurrence occurrence = new Occurrence();
                    occurrence.setLine(lineNo);
                    occurrence.setStart(m.start()+1);
                    occurrence.setEnd(m.end()+1);
                    occurrence.setIn_sentence(line);
                    occurrences.add(occurrence);
                }
            }
            result.setOccurrences(occurrences);
            result.setNumber_of_occurrences(occurrences.size());
        } catch (FileNotFoundException e) {
            logger.error("file not found on server: ", e);
            throw new AppException("file not found on server");
        } catch (Exception e){
            logger.error("error happened when solving file on server: ", e);
            throw new AppException("error happened when solving file on server");
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    logger.error("failed when closing file : ", e);
                }
            }
            if (sc != null) {
                sc.close();
            }
        }
    }
}
