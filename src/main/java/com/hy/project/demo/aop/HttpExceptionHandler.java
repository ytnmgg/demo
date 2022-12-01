package com.hy.project.demo.aop;

import com.hy.project.demo.exception.DemoException;
import com.hy.project.demo.model.AjaxResult;
import com.hy.project.demo.util.LogUtil;
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

import static com.hy.project.demo.exception.DemoExceptionEnum.UNKNOWN_EXCEPTION;

/**
 * @author rick.wl
 * @date 2021/09/02
 */
@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class HttpExceptionHandler {
    private final static Logger LOGGER = LoggerFactory.getLogger(HttpExceptionHandler.class);

    @ResponseBody
    @ExceptionHandler(value = {Throwable.class})
    public ResponseEntity handleException(final Exception ex, final WebRequest req) {
        LOGGER.error("request error", ex);
        AjaxResult result;
        if (ex instanceof DemoException) {
            result = AjaxResult.fail(((DemoException)ex).getCode().getCode(), ex.getMessage());
        } else {
            //其他错误
            result = AjaxResult.fail(UNKNOWN_EXCEPTION.getCode(), UNKNOWN_EXCEPTION.getDescription());
        }

        LogUtil.logHttp(result.getCode(), String.valueOf(result.getData()), ex);

        return new ResponseEntity<Object>(result, HttpStatus.OK);
    }
}
