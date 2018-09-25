package com.jadenyangca.exercise.component;

import org.springframework.stereotype.Component;

import java.util.List;

/**
 * the result will be tranformed to json to user
 * @author Jaden
 * @create 2018-09-24
 */
@Component
public class Result {
    private String query_text;
    private Integer number_of_occurrences;
    private List<Occurrence>  occurrences;

    public Integer getNumber_of_occurrences() {
        return number_of_occurrences;
    }

    public void setNumber_of_occurrences(Integer number_of_occurrences) {
        this.number_of_occurrences = number_of_occurrences;
    }

    public String getQuery_text() {
        return query_text;
    }

    public void setQuery_text(String query_text) {
        this.query_text = query_text;
    }

    public List<Occurrence> getOccurrences() {
        return occurrences;
    }

    public void setOccurrences(List<Occurrence> occurrences) {
        this.occurrences = occurrences;
    }
}
