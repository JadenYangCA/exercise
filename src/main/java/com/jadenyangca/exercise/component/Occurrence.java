package com.jadenyangca.exercise.component;

import org.springframework.stereotype.Component;

/**
 * matching information
 * @author Jaden
 * @create 2018-09-24
 */
@Component
public class Occurrence {
    private long line;
    private int start;
    private int end;
    private String in_sentence;

    public String getIn_sentence() {
        return in_sentence;
    }

    public void setIn_sentence(String in_sentence) {
        this.in_sentence = in_sentence;
    }

    public long getLine() {
        return line;
    }

    public void setLine(long line) {
        this.line = line;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }
}
