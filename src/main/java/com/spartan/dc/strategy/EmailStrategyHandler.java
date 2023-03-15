package com.spartan.dc.strategy;

import com.spartan.dc.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * StrategyHandler:Policy processing class [can be understood as a policy factory class]
 *
 * @author rjx
 * @date 2022/10/10 16:17
 */
@Component
@Slf4j
public class EmailStrategyHandler implements InitializingBean, ApplicationContextAware {
    /**
     * The map where the policy is stored, which can be considered as a registry for the policy
     */
    private final Map<String, EmailService> emailServiceMap = new ConcurrentHashMap<>(16);
    /**
     * spring's context
     */
    private ApplicationContext applicationContext;

    /**
     * Put the StrategyService classes into the strategyServiceMap according to the defined rules (fetchKey)
     */
    @Override
    public void afterPropertiesSet() {
        // initialize all the strategy beans into the ioc, for use when fetching
        Map<String, EmailService> matchBeans = applicationContext.getBeansOfType(EmailService.class);
        // Use the bean injected by the policy as the key, and the policy implementation class as the value
        matchBeans.forEach((key, value) -> {
            emailServiceMap.put(value.fetchKey(), value);
            log.info("Initialize the key-value pairs of the strategy pattern: key={},value={}", key, value);
        });
    }

    /**
     * applicationContext
     *
     * @param applicationContext ac
     * @throws BeansException e
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * Get the corresponding policy implementation by key
     *
     * @param key key(String type or shape-shifting can be used, just keep the same as the policy interface)
     * @return strategyService
     */
    public EmailService getStrategy(String key) {
        if (null == emailServiceMap.get(key)) {
            // Default policy
            return emailServiceMap.get(0);
        }
        return emailServiceMap.get(key);
    }
}