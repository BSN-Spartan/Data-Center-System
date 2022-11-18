package com.spartan.dc.delayed.producer;

import com.spartan.dc.core.enums.DelayedTaskTypeEnum;
import com.spartan.dc.core.enums.DelayedTimeEnum;
import com.spartan.dc.delayed.DelayQueueHelper;
import com.spartan.dc.delayed.DelayedTask;
import com.spartan.dc.delayed.entity.QueryPayResultEntity;
import com.spartan.dc.handler.PaymentHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * Query payment result producer
 *
 * @Author : rjx
 * @Date : 2022/10/11 14:13
 **/
@Slf4j
@Component
public class QueryPayResultProducer {

    /**
     * Defer queue
     */
    private DelayQueueHelper<QueryPayResultEntity> delayQueueHelper = DelayQueueHelper.getInstance();

    private static final Integer maxPollTime = 15;

    @Resource
    private PaymentHandler paymentHandler;

    public boolean execute(QueryPayResultEntity queryPayResultEntity) {
        log.info("Payment result query --- Order [{}] query result task starts production", queryPayResultEntity.getTradeNo());
        Integer number = Objects.isNull(queryPayResultEntity.getNumber()) ? 0 : queryPayResultEntity.getNumber();
        if (number > maxPollTime) {
            log.info("Payment result query --- Order [{}] exceeds the maximum number of queries", queryPayResultEntity.getTradeNo());
            // If exceeds the maximum number of queries, then set the order status to fail
            paymentHandler.updateOrderToFail(queryPayResultEntity.getTradeNo());
            return false;
        }

        // Match delay time
        Long delayedTime;
        if (number > DelayedTimeEnum.TEN.getCode()) {
            delayedTime = DelayedTimeEnum.TEN.getValue();
        } else {
            delayedTime = DelayedTimeEnum.getEnumByCode(number).getValue();
        }

        // Assemble the delayed task and put it into the queue
        DelayedTask<QueryPayResultEntity> delayedTask = new DelayedTask<>(DelayedTaskTypeEnum.QUERY_PAY_RESULT.getCode(), queryPayResultEntity, delayedTime);
        delayQueueHelper.addTask(delayedTask);

        log.info("Payment result query --- Billing [{}] query result task has been put into the delay queue", queryPayResultEntity.getTradeNo());
        return true;
    }

}
