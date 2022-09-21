package com.hy.project.demo.util;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;

import com.hy.project.demo.model.AjaxResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author rick.wl
 * @date 2022/09/13
 */
public class ResultUtil {
    private final static Logger LOGGER = LoggerFactory.getLogger(ResultUtil.class);

    // OK
    public static final String CODE_200 = "200";
    // Move Temporarily
    public static final String CODE_302 = "302";
    // Bad Request
    public static final String CODE_400 = "400";
    // Unauthorized
    public static final String CODE_401 = "401";
    // Forbidden
    public static final String CODE_403 = "403";
    // Internal Server Error
    public static final String CODE_500 = "500";

    public static void buildJsonResponse(HttpServletResponse httpServletResponse, AjaxResult result) {
        PrintWriter writer = null;
        try {
            httpServletResponse.setStatus(200);
            httpServletResponse.setContentType("application/json");
            httpServletResponse.setCharacterEncoding("utf-8");
            writer = httpServletResponse.getWriter();
            writer.write(JSON.toJSONString(result));
            writer.flush();
        } catch (IOException e) {
            LOGGER.error("build json response error", e);
        } finally {
            if (null != writer) {
                writer.close();
            }
        }
    }

}
