package com.spartan.dc.task;

import com.alibaba.fastjson.JSONObject;
import com.reddate.spartan.SpartanSdkClient;
import com.reddate.spartan.dto.spartan.RechgInfo;
import com.spartan.dc.dao.write.DcGasRechargeRecordMapper;
import com.spartan.dc.dao.write.SysDataCenterMapper;
import com.spartan.dc.core.util.enums.RechargeStateEnum;
import com.spartan.dc.model.DcGasRechargeRecord;
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

/**
 * Query the energy value recharge result
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

    @Scheduled(cron = "${task.gasRechargeResult}")
    private void configureTasks() throws Exception {

        SysDataCenter sysDataCenter = sysDataCenterMapper.getSysDataCenter();
        if (sysDataCenter == null) {
            LOG.info("Task Query the energy value recharge result fail: {}", "the basic information of data center is not configured");
            return;
        }

        if (!walletService.checkWalletExists()) {
            LOG.info("Task Query the energy value recharge result fail: {}", "the keystore information is not configured");
            return;
        }

        List<DcGasRechargeRecord> dcGasRechargeRecords = dcGasRechargeRecordMapper.getSuccessSubmit();
        LOG.info("Task Query the energy value recharge result: {}", JSONObject.toJSONString(dcGasRechargeRecords));
        if (dcGasRechargeRecords == null || dcGasRechargeRecords.size() == 0) {
            return;
        }

        String nttAccountAddress = sysDataCenter.getNttAccountAddress();

        for (DcGasRechargeRecord dcGasRechargeRecord : dcGasRechargeRecords) {

            RechgInfo rechgInfo = getRechargeResultByRechargeCode(dcGasRechargeRecord.getRechargeCode());
            if (rechgInfo == null || !rechgInfo.getDcAcc().equalsIgnoreCase(nttAccountAddress)) {
                continue;
            }

            if (Integer.toString(rechgInfo.getStatus()).equals(RechargeStateEnum.SUCCESSFUL.getCode().toString())) {
                dcGasRechargeRecord.setRechargeState(RechargeStateEnum.SUCCESSFUL.getCode());
                dcGasRechargeRecord.setUpdateTime(new Date());
                dcGasRechargeRecord.setNtt(rechgInfo.getNttAmt());
                dcGasRechargeRecord.setRechargeResult(dcGasRechargeRecord.getTxHash());
            } else if (Integer.toString(rechgInfo.getStatus()).equals(RechargeStateEnum.FAILED.getCode().toString())) {
                dcGasRechargeRecord.setRechargeState(RechargeStateEnum.FAILED.getCode());
                dcGasRechargeRecord.setUpdateTime(new Date());
                dcGasRechargeRecord.setNtt(rechgInfo.getNttAmt());
                dcGasRechargeRecord.setRechargeResult(rechgInfo.getRemark());
            } else {
                continue;
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




















