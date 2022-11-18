package com.spartan.dc.config;


import com.google.common.collect.Lists;
import com.reddate.spartan.SpartanSdkClient;
import com.reddate.spartan.constant.ErrorMessage;
import com.reddate.spartan.exception.SpartanException;
import com.reddate.spartan.listener.SignEventListener;
import com.reddate.spartan.net.SpartanGovern;
import com.spartan.dc.core.dto.portal.SendMessageReqVO;
import com.spartan.dc.core.enums.MsgCodeEnum;
import com.spartan.dc.core.util.common.CacheManager;
import com.spartan.dc.dao.write.SysDataCenterMapper;
import com.spartan.dc.model.SysDataCenter;
import com.spartan.dc.service.SendMessageService;
import com.spartan.dc.service.WalletService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.utils.Numeric;
import org.web3j.utils.Strings;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.spartan.dc.core.util.common.CacheManager.PASSWORD_CACHE_KEY;

/**
 * @author wxq
 * @create 2022/8/10 15:01
 * @description sdk config
 */
@Configuration
@Slf4j
public class NcSdkConfigInfo {

    @Value("${chain.nodeRpcAddress}")
    private String nodeRpcAddress;
    @Value("${chain.chainId}")
    private long chainId;
    @Value("${chain.txPoolSleep}")
    private long txPoolSleep;

    private static short checkKeystorePasswordNo;

    @Autowired
    private WalletService walletService;

    @Autowired
    private SendMessageService sendMessageService;

    @Resource
    private SysDataCenterMapper sysDataCenterMapper;

    @Bean
    public SpartanSdkClient getDDCSdkClient() {
        // check keystore password
        checkKeystorePassword();

        // init sdk
        SignEventListener signEventListener = event -> transactionSignatureByOpbWallet(event.getRawTransaction());

        SpartanSdkClient ddcSdkClient =
                new SpartanSdkClient().instance(signEventListener);
        SpartanGovern.setGatewayUrl(nodeRpcAddress);
        SpartanGovern.setTxPoolSleepTime(txPoolSleep);
        return ddcSdkClient;
    }

    private String transactionSignatureByOpbWallet(RawTransaction transaction) {
        if (Objects.isNull(transaction)) {
            throw new SpartanException(ErrorMessage.UNKNOWN_ERROR.getCode(), "transactionSignature error");
        }


        String pwd = CacheManager.get(PASSWORD_CACHE_KEY);
        if (Strings.isEmpty(pwd)) {
            throw new SpartanException(ErrorMessage.UNKNOWN_ERROR.getCode(), "keystore password is null");
        }

        Credentials credentials = null;
        try {
            credentials = walletService.loadWallet(pwd);
        } catch (Exception e) {
            throw new SpartanException(ErrorMessage.UNKNOWN_ERROR.getCode(), "keystore password load error");
        }

        if (credentials == null) {
            throw new SpartanException(ErrorMessage.UNKNOWN_ERROR.getCode(), "keystore password credentials is null");
        }

        byte[] signedMessage = TransactionEncoder.signMessage(transaction, chainId, credentials);
        return Numeric.toHexString(signedMessage);
    }

    public String getNodeRpcAddress() {
        return nodeRpcAddress;
    }

    public void setNodeRpcAddress(String nodeRpcAddress) {
        this.nodeRpcAddress = nodeRpcAddress;
    }

    public long getChainId() {
        return chainId;
    }

    public void setChainId(long chainId) {
        this.chainId = chainId;
    }

    public long getTxPoolSleep() {
        return txPoolSleep;
    }

    public void setTxPoolSleep(long txPoolSleep) {
        this.txPoolSleep = txPoolSleep;
    }


    private void checkKeystorePassword() {
        try {
            if (StringUtils.isEmpty(CacheManager.get(PASSWORD_CACHE_KEY)) && checkKeystorePasswordNo == 0) {
                SysDataCenter sysDataCenter = sysDataCenterMapper.getSysDataCenter();
                if (sysDataCenter == null) {
                    log.info("check keystore password: {}", "the basic information of the data center has not been configured yet");
                    return;
                }
                log.info("check keystore password email......");
                if (org.apache.commons.lang.StringUtils.isBlank(sysDataCenter.getContactsEmail())) {
                    log.error(String.format("Send keystore email error,dc_code:%s,is empty.", sysDataCenter.getDcCode()));
                    return;
                }
                // Assemble mail parameters
                Map<String, Object> replaceContentMap = new HashMap<>();
                replaceContentMap.put("dc_code_", sysDataCenter.getDcCode());

                // Recipient
                List<String> receivers = Lists.newArrayList();
                receivers.add(sysDataCenter.getContactsEmail());

                // Send email
                sendEmail(MsgCodeEnum.KEY_STORE_PASSWORD.getCode(), replaceContentMap, receivers);

                log.info("check keystore password:mail sent");
                checkKeystorePasswordNo = 1;
            }
        } catch (Exception e) {
            log.error("check keystore password send email error:", e);
        }
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
