package com.spartan.dc.core.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;


@Aspect
@Component
public class HttpAspect {

    private final static Logger logger = LoggerFactory.getLogger(HttpAspect.class);

    @Pointcut("execution(public * com.spartan.dc.controller.*.*(..))")
    public void log() {
    }

    @Before("log()")
    public void doBefore(JoinPoint joinPoint) {
        handleBeforeLog("BSN Spartan Data Center System", joinPoint);
    }

    @AfterReturning(returning = "object", pointcut = "log()")
    public void doAfterReturning(Object object) {
        handleAfterLog("BSN Spartan Data Center System", object);
    }

    private void handleAfterLog(String logPrefix, Object object) {
        logger.info("【{}】\t result：\t 【{}】", logPrefix, object);
    }

    private void handleBeforeLog(String logPrefix, JoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        logger.info("【{}】\turl = 【{}】 \tmethod = 【{}】 \tIP = 【{}】 \tclass_method = 【{}】",
                logPrefix, request.getRequestURI(), request.getMethod(), request.getRemoteAddr(),
                joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
    }


}
