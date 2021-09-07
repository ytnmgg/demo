package com.hy.project.demo.controller;

import java.util.HashMap;
import java.util.Map;

import com.hy.project.demo.exception.DemoException;
import com.hy.project.demo.util.HttpClientUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

/**
 * @author rick.wl
 * @date 2021/09/02
 */
@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ExceptionHandlerController {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientUtil.class);

    @ResponseBody
    @ExceptionHandler(value = {Throwable.class})
    public ResponseEntity handleException(final Exception ex, final WebRequest req) {
        Map<String, String> errorMap = new HashMap<>();

        if (ex instanceof DemoException) {
            errorMap.put("code", ((DemoException)ex).getCode().getCode());
            errorMap.put("message", ex.getMessage());
            return new ResponseEntity<Object>(errorMap, HttpStatus.OK);
        }

        //其他错误
        errorMap.put("code", "000");
        errorMap.put("message", "系统错误,请稍后再试");
        return new ResponseEntity<Object>(errorMap, HttpStatus.OK);
    }
}
