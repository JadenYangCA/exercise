package com.jadenyangca.exercise.controller;

import com.jadenyangca.exercise.component.Result;
import com.jadenyangca.exercise.exception.RequestException;
import com.jadenyangca.exercise.service.SearchingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Jaden
 * @create 2018-09-24
 */
@Controller
@Api(value = "text", description = "For searching text")
public class SearchingController {

    @Autowired
    SearchingService searchingService;

    @ResponseBody
    @GetMapping("/text/{query_text}")
    @ApiOperation(value = "API for searching text",notes = "API for searching text")
    public Result findLocationsInText(@PathVariable("query_text") String keyWords){
        if(StringUtils.isEmpty(keyWords.trim())){
            throw new RequestException("query text is empty!");
        }
        Result result = new Result();
        result.setQuery_text(keyWords);
        searchingService.searchText(keyWords, result);
        return result;
    }
}
