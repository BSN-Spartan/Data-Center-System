package com.spartan.dc.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.reddate.spartan.SpartanSdkClient;
import com.reddate.spartan.dto.spartan.RechgInfo;
import com.spartan.dc.dao.write.DcGasRechargeRecordMapper;
import com.spartan.dc.dao.write.DcPaymentOrderMapper;
import com.spartan.dc.dao.write.SysDataCenterMapper;
import com.spartan.dc.core.enums.RechargeStateEnum;
import com.spartan.dc.model.DcGasRechargeRecord;
import com.spartan.dc.model.DcPaymentOrder;
import com.spartan.dc.model.SysDataCenter;
import com.spartan.dc.service.WalletService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Query Gas Credit top-up result
 * @author linzijun
 * @version V1.0
 * @date 2022/8/9 15:37
 */
@Configuration
@ConditionalOnProperty(prefix = "task", name = "enabled", havingValue = "true")
public class GasRechargeResultTask {

    private final static Logger LOG = LoggerFactory.getLogger(GasRechargeResultTask.class);

    @Resource
    private DcGasRechargeRecordMapper dcGasRechargeRecordMapper;

    @Autowired
    private SpartanSdkClient spartanSdkClient;

    @Resource
    private SysDataCenterMapper sysDataCenterMapper;

    @Autowired
    private WalletService walletService;

    @Resource
    private DcPaymentOrderMapper dcPaymentOrderMapper;

    @Scheduled(cron = "${task.gasRechargeResult}")
    private void configureTasks() throws Exception {
        SysDataCenter sysDataCenter = sysDataCenterMapper.getSysDataCenter();
        if (sysDataCenter == null) {
            LOG.info("Task: Exception of querying Gas Credit top-up result: {}", "the basic information of the data center has not been configured yet");
            return;
        }

        if (!walletService.checkWalletExists()) {
            LOG.info("Task: Exception of querying Gas Credit top-up result: {}", "the key store information has not been configured yet");
            return;
        }

        List<DcGasRechargeRecord> dcGasRechargeRecords = dcGasRechargeRecordMapper.getSuccessSubmit();
        LOG.info("Task: Query Gas Credit top-up result: {}", JSONObject.toJSONString(dcGasRechargeRecords));
        if (dcGasRechargeRecords == null || dcGasRechargeRecords.size() == 0) {
            return;
        }

        String nttAccountAddress = sysDataCenter.getNttAccountAddress();

        for (DcGasRechargeRecord dcGasRechargeRecord : dcGasRechargeRecords) {

            RechgInfo rechgInfo = getRechargeResultByRechargeCode(dcGasRechargeRecord.getRechargeCode());
            if (rechgInfo == null || !rechgInfo.getDcAcc().equalsIgnoreCase(nttAccountAddress)) {
                continue;
            }

            Long orderId = Objects.isNull(dcGasRechargeRecord.getOrderId()) ? null : dcGasRechargeRecord.getOrderId();
            DcPaymentOrder selectPaymentOrder = dcPaymentOrderMapper.selectByPrimaryKey(orderId);
            DcPaymentOrder dcPaymentOrder = new DcPaymentOrder();

            if (Integer.toString(rechgInfo.getStatus()).equals(RechargeStateEnum.SUCCESSFUL.getCode().toString())) {
                dcGasRechargeRecord.setRechargeState(RechargeStateEnum.SUCCESSFUL.getCode());
                dcGasRechargeRecord.setUpdateTime(new Date());
                dcGasRechargeRecord.setNtt(rechgInfo.getNttAmt());
                dcGasRechargeRecord.setRechargeResult(dcGasRechargeRecord.getTxHash());

                dcPaymentOrder.setOrderId(orderId);
                dcPaymentOrder.setGasTxHash(dcGasRechargeRecord.getTxHash());
                dcPaymentOrder.setGasTxTime(dcGasRechargeRecord.getRechargeTime());
                dcPaymentOrder.setGasRechargeState(RechargeStateEnum.SUCCESSFUL.getCode());
            } else if (Integer.toString(rechgInfo.getStatus()).equals(RechargeStateEnum.FAILED.getCode().toString())) {
                dcGasRechargeRecord.setRechargeState(RechargeStateEnum.FAILED.getCode());
                dcGasRechargeRecord.setUpdateTime(new Date());
                dcGasRechargeRecord.setNtt(rechgInfo.getNttAmt());
                dcGasRechargeRecord.setRechargeResult(rechgInfo.getRemark());

                dcPaymentOrder.setOrderId(orderId);
                dcPaymentOrder.setGasRechargeState(RechargeStateEnum.FAILED.getCode());
            } else {
                continue;
            }

            if (!Objects.isNull(selectPaymentOrder)) {
                dcPaymentOrderMapper.updateByPrimaryKeySelective(dcPaymentOrder);
            }

            dcGasRechargeRecordMapper.updateByPrimaryKeySelective(dcGasRechargeRecord);
        }
    }

    /**
     * Obtain the recharge result
     * @param rechargeCode
     */
    private RechgInfo getRechargeResultByRechargeCode(String rechargeCode) throws Exception {
        RechgInfo rechgInfo = spartanSdkClient.gasCreditRechargeService.getGCRechg(rechargeCode);
        return rechgInfo;
    }
}
