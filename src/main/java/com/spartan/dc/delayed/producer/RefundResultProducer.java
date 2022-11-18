package com.spartan.dc.delayed.producer;

import com.spartan.dc.core.enums.DelayedTaskTypeEnum;
import com.spartan.dc.core.enums.DelayedTimeEnum;
import com.spartan.dc.delayed.DelayQueueHelper;
import com.spartan.dc.delayed.DelayedTask;
import com.spartan.dc.delayed.entity.RefundResultEntity;
import com.spartan.dc.handler.PaymentHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * Query the payment result producer
 *
 * @Author : rjx
 * @Date : 2022/10/11 14:13
 **/
@Slf4j
@Component
public class RefundResultProducer {

    /**
     * Delay queue
     */
    private DelayQueueHelper<RefundResultEntity> delayQueueHelper = DelayQueueHelper.getInstance();

    private static final Integer maxPollTime = 15;

    @Resource
    private PaymentHandler paymentHandler;

    public boolean execute(RefundResultEntity refundResultEntity) {
        log.info("Refund Result Enquiry---order 【{}】 the query result task starts production", refundResultEntity.getTradeNo());
        Integer number = Objects.isNull(refundResultEntity.getNumber()) ? 0 : refundResultEntity.getNumber();
        if (number > maxPollTime) {
            log.info("Refund Result Enquiry---order【{}】 exceed the maximum number of queries", refundResultEntity.getTradeNo());
            // Exceeding the maximum number of queries will change the order status to failure
            paymentHandler.updateOrderToFail(refundResultEntity.getTradeNo());
            return false;
        }

        // Matching delay time
        Long delayedTime;
        if (number > DelayedTimeEnum.TEN.getCode()) {
            delayedTime = DelayedTimeEnum.TEN.getValue();
        } else {
            delayedTime = DelayedTimeEnum.getEnumByCode(number).getValue();
        }

        // Assemble the delayed tasks and queue them
        DelayedTask<RefundResultEntity> delayedTask = new DelayedTask<>(DelayedTaskTypeEnum.QUERY_REFUND_RESULT.getCode(), refundResultEntity, delayedTime);
        delayQueueHelper.addTask(delayedTask);

        log.info("Refund Result Enquiry---bill【{}】 the query result task has been placed into the delay queue", refundResultEntity.getTradeNo());
        return true;
    }

}
