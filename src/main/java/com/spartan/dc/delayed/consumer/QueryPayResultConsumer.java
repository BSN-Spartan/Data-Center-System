package com.spartan.dc.delayed.consumer;

import com.alibaba.fastjson.JSON;
import com.spartan.dc.core.enums.DelayedTaskTypeEnum;
import com.spartan.dc.delayed.DelayQueueHelper;
import com.spartan.dc.delayed.DelayedTask;
import com.spartan.dc.delayed.entity.QueryPayResultEntity;
import com.spartan.dc.delayed.producer.QueryPayResultProducer;
import com.spartan.dc.strategy.StrategyHandler;
import com.spartan.dc.strategy.StrategyService;
import com.spartan.dc.tread.DefaultThreadPool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.concurrent.DelayQueue;

/**
 * Query the consumer by payment result
 *
 * @Author : rjx
 * @Date : 2022/10/11 15:08
 **/
@Slf4j
@Component
public class QueryPayResultConsumer implements ApplicationRunner {

    @Resource
    private StrategyHandler strategyHandler;
    @Resource
    private QueryPayResultProducer queryPayResultProducer;

    @Override
    public void run(ApplicationArguments args) {
        DelayQueueHelper<QueryPayResultEntity> delayQueueHelper = DelayQueueHelper.getInstance();

        run(delayQueueHelper.getQueue());
    }

    public void run(DelayQueue<DelayedTask<QueryPayResultEntity>> queue) {
        DefaultThreadPool.asyncTask(() -> {
            while (true) {
                log.debug("Consumer，queue: {}", JSON.toJSONString(queue));
                if (CollectionUtils.isEmpty(queue)) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    continue;
                }
                // Task start consumption
                DelayedTask<QueryPayResultEntity> take;
                try {
                    take = queue.take();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                boolean result = false;
                QueryPayResultEntity data = take.getData();
                // Process the corresponding logic according to the payment type
                StrategyService strategy = strategyHandler.getStrategy(data.getChannelCode());
                if (DelayedTaskTypeEnum.QUERY_PAY_RESULT.getCode().equals(take.getType())) {
                    log.info("Payment result query - Billing [{}] Query task start consumption", data.getTradeNo());
                    result = strategy.queryPayResult(data);
                } else if (DelayedTaskTypeEnum.QUERY_REFUND_RESULT.equals(take.getType())) {
                    log.info("Refund result query - Billing [{}] Query task start consumption", data.getTradeNo());
                    result = strategy.refundResult(data);
                }

                // If the processing result is false, continue to put in the queue and increment the number of times
                if (!result) {
                    data.setNumber(data.getNumber() + 1);
                    queryPayResultProducer.execute(data);
                }

            }
        });

    }

}
