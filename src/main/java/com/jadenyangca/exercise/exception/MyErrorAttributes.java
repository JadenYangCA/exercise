package com.jadenyangca.exercise.exception;

import org.springframework.boot.autoconfigure.web.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;

import java.util.Map;

/**
 * generate defined ErrorAttributes
 * @author Jaden
 * @create 2018-09-24
 */

@Component
public class MyErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(RequestAttributes requestAttributes, boolean includeStackTrace) {
        Map<String, Object> map = super.getErrorAttributes(requestAttributes, includeStackTrace);
        map.put("company","openhouse.ai");
//        Map<String,Object> ext = (Map<String, Object>) requestAttributes.getAttribute("ext", 0);
//        map.put("ext",ext);

        //return the map for page and json string
        return map;
    }
}