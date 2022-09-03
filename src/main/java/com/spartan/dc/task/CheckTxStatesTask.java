package com.spartan.dc.task;

import com.reddate.spartan.dto.wuhanchain.TransactionsBean;
import com.spartan.dc.core.dto.dc.GasRechargeRecordDTO;
import com.spartan.dc.core.dto.task.req.GasRechargeRecordReqVO;
import com.spartan.dc.dao.write.DcGasRechargeRecordMapper;
import com.spartan.dc.core.util.enums.RechargeSubmitStateEnum;
import org.fisco.bcos.web3j.utils.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

import static com.reddate.spartan.SpartanSdkClient.spartanSdkClient;

/**
 * @author wxq
 * @create 2022/8/22 15:45
 * @description check tx states
 */
@Configuration
@ConditionalOnProperty(prefix = "task", name = "enabled", havingValue = "true")
public class CheckTxStatesTask {

    private final static Logger LOG = LoggerFactory.getLogger(CheckTxStatesTask.class);

    @Resource
    private DcGasRechargeRecordMapper dcGasRechargeRecordMapper;

    @Scheduled(cron = "${task.checkTxStates}")
    private void configureTasks() {
        GasRechargeRecordReqVO recordReqVO = new GasRechargeRecordReqVO();
        recordReqVO.setState(RechargeSubmitStateEnum.SUBMISSION_FAILED.getCode());

        try {
            List<GasRechargeRecordDTO> gasRechargeRecords = dcGasRechargeRecordMapper.getSuccessSubmitAndRechargingRecords(recordReqVO);

            for (GasRechargeRecordDTO record : gasRechargeRecords) {
                //
                if (Strings.isEmpty(record.getTxHash())) {
                    LOG.error("Task CheckTxStates txHash is empty,rechargeId:{}", record.getRechargeRecordId());
                    continue;
                }
                TransactionsBean transactionsBean = spartanSdkClient.baseService.getTransactionByHash(record.getTxHash());
                if (Objects.isNull(transactionsBean)) {
                    //
                    record.setState(RechargeSubmitStateEnum.PENDING_SUBMIT.getCode());
                    dcGasRechargeRecordMapper.updateRechargeStateById(record);
                } else {
                    //
                    record.setState(RechargeSubmitStateEnum.SUBMITTED_SUCCESSFULLY.getCode());
                    dcGasRechargeRecordMapper.updateRechargeStateById(record);
                }
            }
        } catch (Exception e) {
            LOG.error("Task CheckTxStates Error:", e);
        }

    }
}
