package com.jadenyangca.exercise.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @author Jaden
 * @create 2018-09-24
 */
@ControllerAdvice
public class AppExceptionHandler {
    //    @ResponseBody
    @ExceptionHandler(AppException.class)
    public String handleException(Exception e, HttpServletRequest request) {
       /* Map<String, Object> map = new HashMap<>();
        map.put("code", e.getMessage());
        map.put("message", "error happened on server");
        request.setAttribute("ext", map);*/

//        //input defined error code:  4xx 5xx
        request.setAttribute("javax.servlet.error.status_code", 500);

        //BasicErrorController will solve the error, ErrorAttributes
        return "forward:/error";
//        return map;
    }
}
