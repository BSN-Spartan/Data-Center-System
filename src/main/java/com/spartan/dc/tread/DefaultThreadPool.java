package com.spartan.dc.tread;

import com.spartan.dc.tread.context.ApplicationContextUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;


public abstract class DefaultThreadPool {

    private static final String DEFAULT_THREAD_POOL_NAME = "defaultThreadPoolTaskExecutor" ;


    public static ThreadPoolTaskExecutor getDefaultPool() {
        return ApplicationContextUtils.getBean(DEFAULT_THREAD_POOL_NAME,ThreadPoolTaskExecutor.class);
    }


    public static <T> CompletableFuture<T> asyncTask(Supplier<T> supplier) {
        return CompletableFuture.supplyAsync(supplier, getDefaultPool());
    }

}
