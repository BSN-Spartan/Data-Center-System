package com.spartan.dc.tread;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;


@Configuration
@EnableAsync
@Slf4j
@ConditionalOnProperty(prefix = "thread-pool.cfg", name = "switch", havingValue = "true")
public class ThreadPoolConfig {


    @Value("${thread-pool.cfg.core-size:20}")
    private Integer corePoolSize;

    @Value("${thread-pool.cfg.max-size:30}")
    private Integer maxPoolSize;


    @Value("${thread-pool.cfg.keep-alive:1000}")
    private Integer KeepAliveSeconds;


    @Value("${thread-pool.cfg.queue_capacity:500}")
    private Integer queueCapacity;


    @Value("${thread-pool.cfg.prefix:default-task}")
    private String threadNamePrefix;


    @Bean(name = "defaultThreadPoolTaskExecutor")
    public Executor defaultThreadPoolTaskExecutor() {
        log.info("=================start  defaultThreadPoolTaskExecutor==========================");
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(corePoolSize);

        executor.setMaxPoolSize(maxPoolSize);

        executor.setQueueCapacity(queueCapacity);

        executor.setThreadNamePrefix(threadNamePrefix);

        executor.setKeepAliveSeconds(KeepAliveSeconds);

        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        executor.initialize();
        return executor;
    }

}
