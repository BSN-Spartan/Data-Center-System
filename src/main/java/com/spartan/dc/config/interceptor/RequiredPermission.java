package com.spartan.dc.config.interceptor;

import java.lang.annotation.*;


@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
public @interface RequiredPermission {

    boolean validate() default true;

    boolean isPage() default false;
}
