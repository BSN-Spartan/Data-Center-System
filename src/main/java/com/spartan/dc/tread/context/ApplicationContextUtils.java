package com.spartan.dc.tread.context;


import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @ClassName a
 * @Description ApplicationContextUtils
 * @Author yll
 * @Date 13:55 2022/7/13
 * @Version 1.0
 **/
@Component
public class ApplicationContextUtils implements ApplicationContextAware {

    private static ApplicationContext context;

    public ApplicationContextUtils() {
    }

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    public static Object getBean(String beanName)  {
        return context.getBean(beanName);
    }

    public static <T> T getBean(Class<T> type) {
        return context.getBean(type);
    }

    public static <T> T getBean(String beanName, Class<T> type)  {
        return context.getBean(beanName,type);
    }


}