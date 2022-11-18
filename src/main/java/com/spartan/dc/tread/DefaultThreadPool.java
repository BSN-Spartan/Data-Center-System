package com.spartan.dc.tread;

import com.spartan.dc.tread.context.ApplicationContextUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * @ClassName DefaultThreadPool
 * @Description The default thread pool call uses the class
 * @Author yll
 * @Date 14:06 2022/7/13
 * @Version 1.0
 **/
public abstract class DefaultThreadPool {

    private static final String DEFAULT_THREAD_POOL_NAME = "defaultThreadPoolTaskExecutor" ;


    /**
     * Get the default thread pool, you need to open the thread pool configuration before using
     * @return
     */
    public static ThreadPoolTaskExecutor getDefaultPool() {
        return ApplicationContextUtils.getBean(DEFAULT_THREAD_POOL_NAME,ThreadPoolTaskExecutor.class);
    }


    /**
     * Asynchronous execution and get the return value
     * @param supplier
     * @param <T>
     * @return
     */
    public static <T> CompletableFuture<T> asyncTask(Supplier<T> supplier) {
        return CompletableFuture.supplyAsync(supplier, getDefaultPool());
    }

}
