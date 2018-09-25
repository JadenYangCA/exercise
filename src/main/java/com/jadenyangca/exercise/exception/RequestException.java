package com.jadenyangca.exercise.exception;

/**
 * @author Jaden
 * @create 2018-09-25
 */
public class RequestException extends RuntimeException {
    public RequestException(String exceptionContent) {
        super(exceptionContent);
    }
}
