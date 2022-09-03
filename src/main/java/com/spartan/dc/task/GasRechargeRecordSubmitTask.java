package com.spartan.dc.task;

import com.alibaba.fastjson.JSONObject;
import com.reddate.spartan.SpartanSdkClient;
import com.reddate.spartan.dto.wuhanchain.ReqJsonWithOfflineHashBean;
import com.spartan.dc.core.dto.task.req.GasRechargeReqVO;
import com.spartan.dc.dao.write.DcGasRechargeRecordMapper;
import com.spartan.dc.dao.write.SysDataCenterMapper;
import com.spartan.dc.core.util.enums.RechargeStateEnum;
import com.spartan.dc.core.util.enums.RechargeSubmitStateEnum;
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

import static com.reddate.spartan.service.BaseService.nonceManagerUtils;

/**
 * Energy value recharge is submitted on the chain
 *
 * @author linzijun
 * @version V1.0
 * @date 2022/8/8 18:19
 */
@Configuration
@ConditionalOnProperty(prefix = "task", name = "enabled", havingValue = "true")
public class GasRechargeRecordSubmitTask {

    private final static Logger LOG = LoggerFactory.getLogger(GasRechargeRecordSubmitTask.class);

    @Resource
    private DcGasRechargeRecordMapper dcGasRechargeRecordMapper;

    @Resource
    private SysDataCenterMapper sysDataCenterMapper;

    @Autowired
    private SpartanSdkClient spartanSdkClient;

    @Autowired
    private WalletService walletService;

    @Scheduled(cron = "${task.gasRechargeSubmit}")
    private void configureTasks() throws Exception {

        SysDataCenter sysDataCenter = sysDataCenterMapper.getSysDataCenter();
        if (sysDataCenter == null) {
            LOG.info("Task Request Gas Recharge Contract fail: {}", "the basic information of data center is not configured");
            return;
        }

        if (!walletService.checkWalletExists()) {
            LOG.info("Task Request Gas Recharge Contract fail: {}", "the keystore information is not configured");
            return;
        }

        List<DcGasRechargeRecord> dcGasRechargeRecords = dcGasRechargeRecordMapper.getUnSubmitGasRechargeRecord();
        LOG.info("Task Request Gas Recharge Contract: {}", JSONObject.toJSONString(dcGasRechargeRecords));
        if (dcGasRechargeRecords == null || dcGasRechargeRecords.size() == 0) {
            return;
        }

        String sendNttAccountAddress = sysDataCenter.getNttAccountAddress();

        for (DcGasRechargeRecord dcGasRechargeRecord : dcGasRechargeRecords) {
            GasRechargeReqVO gasRechargeReqVO = new GasRechargeReqVO();
            gasRechargeReqVO.setSender(sendNttAccountAddress);
            gasRechargeReqVO.setReceiver(dcGasRechargeRecord.getChainAddress());
            gasRechargeReqVO.setChainID(Integer.parseInt(dcGasRechargeRecord.getChainId().toString()));
            gasRechargeReqVO.setEngAmt(dcGasRechargeRecord.getGas().toBigInteger());

            dcGasRechargeRecord.setUpdateTime(new Date());
            try {
                gasRecharge(gasRechargeReqVO, dcGasRechargeRecord);
            } catch (Exception e) {
                LOG.error("Task Request Gas Recharge Contract Fail: {}", e.getMessage());
                dcGasRechargeRecord.setRechargeResult(e.getMessage());
                dcGasRechargeRecord.setState(RechargeSubmitStateEnum.SUBMISSION_FAILED.getCode());
                dcGasRechargeRecord.setRechargeState(RechargeStateEnum.FAILED.getCode());
                dcGasRechargeRecordMapper.updateByPrimaryKeySelective(dcGasRechargeRecord);

                // reset nonce
                nonceManagerUtils.resetNonce();
                continue;
            }

            dcGasRechargeRecord.setState(RechargeSubmitStateEnum.SUBMITTING.getCode());
            dcGasRechargeRecord.setRechargeState(RechargeStateEnum.PENDING.getCode());
            dcGasRechargeRecordMapper.updateByPrimaryKeySelective(dcGasRechargeRecord);
        }

    }

    /**
     * Request Gas Recharge Contract
     *
     * @param gasRechargeReqVO
     * @return
     */
    private void gasRecharge(GasRechargeReqVO gasRechargeReqVO, DcGasRechargeRecord dcGasRechargeRecord) throws Exception {
        ReqJsonWithOfflineHashBean reqJsonWithOfflineHashBean = spartanSdkClient.gasCreditRechargeService.gcRechg(gasRechargeReqVO.getSender(), gasRechargeReqVO.getReceiver(), gasRechargeReqVO.getEngAmt(), gasRechargeReqVO.getChainID());
        String offLineHash = reqJsonWithOfflineHashBean.getOffLineHash();
        dcGasRechargeRecord.setTxHash(offLineHash);

        spartanSdkClient.gasCreditRechargeService.send(reqJsonWithOfflineHashBean.getReqJsonRpcBean());
    }

}