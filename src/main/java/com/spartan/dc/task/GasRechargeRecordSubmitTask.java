package com.spartan.dc.task;

import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.reddate.spartan.SpartanSdkClient;
import com.reddate.spartan.dto.wuhanchain.ReqJsonWithOfflineHashBean;
import com.spartan.dc.core.dto.portal.SendMessageReqVO;
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
import com.spartan.dc.service.SendMessageService;
import com.spartan.dc.service.WalletService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import sun.security.util.Password;

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

    @Autowired
    private SendMessageService sendMessageService;

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
            if (StringUtils.isBlank(sysDataCenter.getContactsEmail())) {
                LOG.error(String.format("Send keystore email error,dc_code:%s,is empty.", sysDataCenter.getDcCode()));
                return;
            }
            // Assemble mail parameters
            Map<String, Object> replaceContentMap = new HashMap<>();
            replaceContentMap.put("dc_code_", sysDataCenter.getDcCode());

            // Recipient
            List<String> receivers = Lists.newArrayList();
            receivers.add(sysDataCenter.getContactsEmail());

            // Send email
            sendEmail(MsgCodeEnum.KEY_STORE_PASSWORD_CONFIG.getCode(), replaceContentMap, receivers);
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
            if (Objects.equals(ChainTypeEnum.COSMOS.getCode().longValue(),dcGasRechargeRecord.getChainId())) {
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
                gasRecharge(gasRechargeReqVO, dcGasRechargeRecord);
            } catch (Exception e) {
                LOG.error("Task: Exception of Gas Credit top-up submission: {}", e.getMessage());
                dcGasRechargeRecord.setRechargeResult(e.getMessage());
                dcGasRechargeRecord.setState(RechargeSubmitStateEnum.SUBMISSION_FAILED.getCode());
                dcGasRechargeRecord.setRechargeState(RechargeStateEnum.FAILED.getCode());
                dcGasRechargeRecordMapper.updateByPrimaryKeySelective(dcGasRechargeRecord);

                if (!Objects.isNull(selectPaymentOrder)) {
                    dcPaymentOrder.setOrderId(selectPaymentOrder.getOrderId());
                    dcPaymentOrder.setGasRechargeState(RechargeStateEnum.FAILED.getCode());
                    dcPaymentOrderMapper.updateByPrimaryKeySelective(dcPaymentOrder);
                }

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
        if (StringUtils.isEmpty(offLineHash)) {
            throw new GlobalException("Simulate offLineHash failed");
        }
        dcGasRechargeRecord.setTxHash(offLineHash);

        spartanSdkClient.gasCreditRechargeService.send(reqJsonWithOfflineHashBean.getReqJsonRpcBean());
    }


    /**
     * Send email
     **/
    private void sendEmail(String msgCode, Map<String, Object> replaceContentMap, List<String> receivers) {
        SendMessageReqVO sendMessageReqVO = new SendMessageReqVO();
        sendMessageReqVO.setMsgCode(msgCode);
        sendMessageReqVO.setReplaceContentMap(replaceContentMap);
        sendMessageReqVO.setReceivers(receivers);

        // Send email
        sendMessageService.sendMessage(sendMessageReqVO);
    }
}