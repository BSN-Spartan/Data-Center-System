package com.spartan.dc.task;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.reddate.spartan.SpartanSdkClient;
import com.reddate.spartan.dto.wuhanchain.ReqJsonWithOfflineHashBean;
import com.reddate.spartan.dto.wuhanchain.TransactionsBean;
import com.spartan.dc.core.dto.task.req.GasRechargeReqVO;
import com.spartan.dc.core.enums.ChainTypeEnum;
import com.spartan.dc.core.enums.MsgCodeEnum;
import com.spartan.dc.core.exception.GlobalException;
import com.spartan.dc.core.util.bech32.Bech32Utils;
import com.spartan.dc.core.util.common.CacheManager;
import com.spartan.dc.dao.write.DcGasRechargeRecordMapper;
import com.spartan.dc.dao.write.DcPaymentOrderMapper;
import com.spartan.dc.dao.write.SysDataCenterMapper;
import com.spartan.dc.core.enums.RechargeStateEnum;
import com.spartan.dc.core.enums.RechargeSubmitStateEnum;
import com.spartan.dc.model.DcGasRechargeRecord;
import com.spartan.dc.model.DcPaymentOrder;
import com.spartan.dc.model.SysDataCenter;
import com.spartan.dc.service.WalletService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;
import java.util.*;

import static com.reddate.spartan.service.BaseService.nonceManagerUtils;
import static com.spartan.dc.core.util.common.CacheManager.PASSWORD_CACHE_KEY;

/**
 * Gas Credit top-up submitted
 *
 * @author linzijun
 * @version V1.0
 * @date 2022/8/8 18:19
 */
@Configuration
@ConditionalOnProperty(prefix = "task", name = "enabled", havingValue = "true")
public class GasRechargeRecordSubmitTask extends BaseTask {

    private final static Logger LOG = LoggerFactory.getLogger(GasRechargeRecordSubmitTask.class);

    @Resource
    private DcGasRechargeRecordMapper dcGasRechargeRecordMapper;

    @Resource
    private SysDataCenterMapper sysDataCenterMapper;

    @Autowired
    private SpartanSdkClient spartanSdkClient;

    @Autowired
    private WalletService walletService;


    @Resource
    private DcPaymentOrderMapper dcPaymentOrderMapper;

    private static short passwordReminderNo;

    private static final String COSMOS_ADDRESS_PREFIX = "iaa";

    @Scheduled(cron = "${task.gasRechargeSubmit}")
    private void configureTasks() throws Exception {
        SysDataCenter sysDataCenter = sysDataCenterMapper.getSysDataCenter();
        if (sysDataCenter == null) {
            LOG.info("Task: Exception of Gas Credit top-up submission: {}", "the basic information of the data center has not been configured yet");
            return;
        }

        if (!walletService.checkWalletExists()) {
            LOG.info("Task: Exception of Gas Credit top-up submission: {}", "the key store information has not been configured yet");
            return;
        }

        List<DcGasRechargeRecord> dcGasRechargeRecords = dcGasRechargeRecordMapper.getUnSubmitGasRechargeRecord();
        LOG.info("Task: Gas Credit top-up submitted: {}", JSONObject.toJSONString(dcGasRechargeRecords));
        if (dcGasRechargeRecords == null || dcGasRechargeRecords.size() == 0) {
            return;
        }

        // If the keystore password is empty，Notify the data center by email
        if (StringUtils.isBlank(CacheManager.get(PASSWORD_CACHE_KEY)) && (passwordReminderNo == 0)) {
            LOG.info("Send keystore email......");

            // keystore password is empty Send email
            emailReminder(sysDataCenter, MsgCodeEnum.KEY_STORE_PASSWORD_CONFIG, null);
            passwordReminderNo = 1;
            return;
        } else if (StringUtils.isBlank(CacheManager.get(PASSWORD_CACHE_KEY))) {
            LOG.error(String.format("Send keystore password is empty,dc_code:%s,is empty.", sysDataCenter.getDcCode()));
            return;
        }

        String sendNttAccountAddress = sysDataCenter.getNttAccountAddress();

        for (DcGasRechargeRecord dcGasRechargeRecord : dcGasRechargeRecords) {
            GasRechargeReqVO gasRechargeReqVO = new GasRechargeReqVO();
            gasRechargeReqVO.setSender(sendNttAccountAddress);
            gasRechargeReqVO.setChainID(Integer.parseInt(dcGasRechargeRecord.getChainId().toString()));
            gasRechargeReqVO.setEngAmt(dcGasRechargeRecord.getGas().toBigInteger());

            String chargeChainAccountAddress = dcGasRechargeRecord.getChainAddress();
            if (Objects.equals(ChainTypeEnum.COSMOS.getCode().longValue(), dcGasRechargeRecord.getChainId())) {
                if (dcGasRechargeRecord.getChainAddress().startsWith("0x")) {
                    // Ox transition iaa
                    chargeChainAccountAddress = Bech32Utils.hexToBech32(COSMOS_ADDRESS_PREFIX, chargeChainAccountAddress.substring(2));
                }
            }
            gasRechargeReqVO.setReceiver(chargeChainAccountAddress);

            dcGasRechargeRecord.setUpdateTime(new Date());

            DcPaymentOrder selectPaymentOrder = Objects.isNull(dcGasRechargeRecord.getOrderId()) ? null : dcPaymentOrderMapper.selectByPrimaryKey(dcGasRechargeRecord.getOrderId());
            DcPaymentOrder dcPaymentOrder = new DcPaymentOrder();
            try {
                // tx_hash not empty. You need to query whether the transaction has been submitted to the chain through hash
                // state = 1 and recharge_state 3 (Newly submitted transactions), submitted directly to the chain
                if (StringUtils.isNotEmpty(dcGasRechargeRecord.getTxHash())) {
                    TransactionsBean tx = spartanSdkClient.baseService.getTransactionByHash(dcGasRechargeRecord.getTxHash());
                    if (Objects.nonNull(tx)) {
                        continue;
                    }
                }
                gasRecharge(gasRechargeReqVO, dcGasRechargeRecord);
            } catch (Exception e) {
                LOG.error("Task: Exception of Gas Credit top-up submission: {}", e.getMessage());

                // reset nonce
                nonceManagerUtils.resetNonce();

                // balance reminder
                if (e.getMessage().contains("insufficient balance")) {
                    if (NTT_BALANCE_REMINDER_TIME == null || DateUtil.between(NTT_BALANCE_REMINDER_TIME, new Date(), DateUnit.MINUTE) >= REMINDER_TIME) {
                        LOG.info("Task: Exception of Gas Credit top-up submission:Send balance reminder email......[insufficient balance]");
                        emailReminder(sysDataCenter, MsgCodeEnum.NTT_BALANCE_REMINDER_CONFIG, null);
                        NTT_BALANCE_REMINDER_TIME = new Date();
                    }
                } else if (e.getMessage().contains("insufficient funds for gas")) {
                    if (GAS_BALANCE_REMINDER_TIME == null || DateUtil.between(GAS_BALANCE_REMINDER_TIME, new Date(), DateUnit.MINUTE) >= REMINDER_TIME) {
                        LOG.info("Task: Exception of Gas Credit top-up submission:Send balance reminder email......[insufficient funds]");
                        emailReminder(sysDataCenter, MsgCodeEnum.GAS_BALANCE_REMINDER_CONFIG, null);
                        GAS_BALANCE_REMINDER_TIME = new Date();
                    }
                } else if (e.getMessage().contains("nonce too low")) {
                    // retry
                } else {
                    dcGasRechargeRecord.setRechargeResult(e.getMessage());
                    dcGasRechargeRecord.setState(RechargeSubmitStateEnum.SUBMISSION_FAILED.getCode());
                    dcGasRechargeRecord.setRechargeState(RechargeStateEnum.FAILED.getCode());
                    dcGasRechargeRecordMapper.updateByPrimaryKeySelective(dcGasRechargeRecord);

                    if (Objects.nonNull(selectPaymentOrder)) {
                        dcPaymentOrder.setOrderId(selectPaymentOrder.getOrderId());
                        dcPaymentOrder.setGasRechargeState(RechargeStateEnum.FAILED.getCode());
                        dcPaymentOrderMapper.updateByPrimaryKeySelective(dcPaymentOrder);
                    }
                }
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
        if (StringUtils.isEmpty(offLineHash)) {
            throw new GlobalException("Simulate offLineHash failed");
        }
        dcGasRechargeRecord.setTxHash(offLineHash);

        spartanSdkClient.gasCreditRechargeService.send(reqJsonWithOfflineHashBean.getReqJsonRpcBean());
    }


}