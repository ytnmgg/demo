package com.hy.project.demo.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hy.project.demo.util.HttpClientUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

/**
 * @author rick.wl
 * @date 2021/09/02
 */
@ControllerAdvice
public class ExceptionHandlerController {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientUtil.class);

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity hadlerException(final Exception ex, final WebRequest req) {
        Map<String, String> errorMap = new HashMap<>();

        LOGGER.error("拦截异常-", ex);

        //参数错误
        if (ex instanceof BindException) {
            BindingResult result = ((BindException)ex).getBindingResult();
            wrapperError(result, errorMap);

            return new ResponseEntity<Object>(errorMap, HttpStatus.OK);
        }

        //其他错误
        errorMap.put("401", "系统错误,请稍后再试");

        return new ResponseEntity<Object>(errorMap, HttpStatus.OK);
    }

    private void wrapperError(BindingResult result, Map<String, String> errorMap) {
        List<ObjectError> list = result.getAllErrors();
        String eMsg = "";
        if (list.size() > 0) {
            eMsg = list.get(0).getDefaultMessage();
        }
        errorMap.put("code", "102");
        errorMap.put("msg", eMsg);
    }

}
