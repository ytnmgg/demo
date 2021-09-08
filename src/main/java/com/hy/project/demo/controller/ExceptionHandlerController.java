package com.hy.project.demo.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hy.project.demo.exception.DemoException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

import static com.hy.project.demo.exception.DemoExceptionEnum.INVALID_PARAM_EXCEPTION;

/**
 * @author rick.wl
 * @date 2021/09/02
 */
@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ExceptionHandlerController {
    public static final String CODE = "code";
    public static final String MSG = "message";

    @ResponseBody
    @ExceptionHandler(value = {Throwable.class})
    public ResponseEntity handleException(final Exception ex, final WebRequest req) {
        Map<String, String> errorMap = new HashMap<>();

        if (ex instanceof DemoException) {
            errorMap.put(CODE, ((DemoException)ex).getCode().getCode());
            errorMap.put(MSG, ex.getMessage());
        } else if (ex instanceof BindException) {
            BindingResult result = ((BindException)ex).getBindingResult();
            wrapperBindError(result, errorMap);
        } else {
            //其他错误
            errorMap.put(CODE, "000");
            errorMap.put(MSG, "系统错误,请稍后再试");
        }

        return new ResponseEntity<Object>(errorMap, HttpStatus.OK);
    }

    private void wrapperBindError(BindingResult result, Map<String, String> errorMap) {
        List<ObjectError> list = result.getAllErrors();
        String eMsg = "";
        if (list.size() > 0) {
            eMsg = list.get(0).getDefaultMessage();
        }
        errorMap.put(CODE, INVALID_PARAM_EXCEPTION.getCode());
        errorMap.put(MSG, eMsg);
    }
}
