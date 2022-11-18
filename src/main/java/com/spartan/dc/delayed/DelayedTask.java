package com.spartan.dc.delayed;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * @Author : rjx
 * @Date : 2022/10/11 10:25
 **/
public class DelayedTask<T> implements Delayed {

    // Business type
    private Integer type;

    // Business data
    private T data;

    // Execution time
    private long executeTime;

    public DelayedTask(Integer type, T data, long executeTime) {
        this.type = type;
        this.data = data;
        this.executeTime = TimeUnit.SECONDS.toMillis(executeTime) + System.currentTimeMillis();
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public long getExecuteTime() {
        return executeTime;
    }

    public void setExecuteTime(long executeTime) {
        this.executeTime = executeTime;
    }

    @Override
    public long getDelay(@NotNull TimeUnit unit) {
        return unit.convert(this.executeTime - System.currentTimeMillis(), TimeUnit.NANOSECONDS);
    }

    @Override
    public int compareTo(@NotNull Delayed o) {
        return Long.compare(this.executeTime, ((DelayedTask<?>) o).executeTime);
    }
}
