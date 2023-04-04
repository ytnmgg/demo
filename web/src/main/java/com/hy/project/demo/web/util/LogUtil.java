package com.hy.project.demo.web.util;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import com.hy.project.demo.web.model.RequestContextHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author rick.wl
 * @date 2022/12/01
 */
public class LogUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogUtil.class);
    private static final Logger HTTP_LOGGER = LoggerFactory.getLogger("httpLogger");

    public static void logHttp(String code, String msg, Throwable error) {
        try {
            HttpServletRequest request = RequestContextHolder.getCurrentRequestContext().getRequest();
            Long startTime = RequestContextHolder.getCurrentRequestContext().getEnterTime();

            JSONObject content = new JSONObject();
            content.put("trace_id", RequestContextHolder.getCurrentRequestContext().getTraceId());
            content.put("request_uri", request.getRequestURI());
            content.put("arguments",
                JSON.toJSONString(RequestContextHolder.getCurrentRequestContext().getRequestArgs()));
            content.put("startTime", startTime);
            content.put("processTime", System.currentTimeMillis() - startTime);
            content.put("code", code);
            content.put("msg", msg);
            //content.put("stack", getErrorStack(error));

            HTTP_LOGGER.info(content.toJSONString());

        } catch (Exception e) {
            LOGGER.error("logHttp error", e);
        }
    }

    private static String getErrorStack(Throwable error) {
        String errorStack = StringUtils.EMPTY;
        if (null != error) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            error.printStackTrace(pw);
            errorStack = sw.toString();
        }
        return errorStack.replace("\n", "#");
    }
}
