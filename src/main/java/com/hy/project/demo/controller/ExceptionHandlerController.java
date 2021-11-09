package com.hy.project.demo.controller;

import com.hy.project.demo.exception.DemoException;
import com.hy.project.demo.model.DemoResult;
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

    @ResponseBody
    @ExceptionHandler(value = {Throwable.class})
    public ResponseEntity handleException(final Exception ex, final WebRequest req) {
        DemoResult<Object> result;
        if (ex instanceof DemoException) {
            result = DemoResult.buildErrorResult(null, ((DemoException)ex).getCode().getCode(), ex.getMessage());
        } else {
            //其他错误
            result = DemoResult.buildErrorResult(null, "001", "系统错误,请稍后再试");
        }
        return new ResponseEntity<Object>(result, HttpStatus.OK);
    }
}
