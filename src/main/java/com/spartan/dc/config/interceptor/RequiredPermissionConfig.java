package com.spartan.dc.config.interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;


@Configuration
public class RequiredPermissionConfig implements WebMvcConfigurer {

    @Resource
    private UserConfigInterceptor userConfigInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userConfigInterceptor);
    }
}
