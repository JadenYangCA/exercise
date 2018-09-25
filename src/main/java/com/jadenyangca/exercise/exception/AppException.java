package com.jadenyangca.exercise.exception;

/**
 * throw this exception if errors happened on server when processing the business logic
 * @author Jaden
 * @create 2018-09-24
 */
public class AppException extends RuntimeException {
    public AppException(String exceptionContent) {
        super(exceptionContent);
    }
}
