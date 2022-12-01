package com.hy.project.demo.aop;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hy.project.demo.model.RequestContext;
import com.hy.project.demo.model.RequestContextHolder;
import com.hy.project.demo.util.LogUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import static com.hy.project.demo.constant.CommonConstants.HTTP_LOG_CODE_SUCCESS;

/**
 * @author rick.wl
 * @date 2022/12/01
 */
@Component
@Aspect
public class HttpLogAdvice {

    @Pointcut("execution(public * com.hy.project.demo.controller..*.*(..))")
    public void httpLog() {
    }

    @Around("httpLog()")
    public Object httpLog(ProceedingJoinPoint point) throws Throwable {
        RequestContext context = RequestContextHolder.getCurrentRequestContext();

        List<Object> requestArgs = new ArrayList<>();

        for (Object arg : point.getArgs()) {
            if (arg instanceof HttpServletRequest || arg instanceof HttpServletResponse) {
                continue;
            }
            requestArgs.add(arg);
        }

        context.setRequestArgs(requestArgs);

        Object result = point.proceed();

        LogUtil.logHttp(HTTP_LOG_CODE_SUCCESS, null, null);

        return result;
    }
}
