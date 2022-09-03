package com.spartan.dc.config;


import com.reddate.spartan.SpartanSdkClient;
import com.reddate.spartan.constant.ErrorMessage;
import com.reddate.spartan.exception.SpartanException;
import com.reddate.spartan.listener.SignEventListener;
import com.reddate.spartan.net.SpartanGovern;
import com.spartan.dc.core.util.common.CacheManager;
import com.spartan.dc.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.utils.Numeric;
import org.web3j.utils.Strings;

import java.util.Objects;

import static com.spartan.dc.core.util.common.CacheManager.PASSWORD_CACHE_KEY;

/**
 * @author wxq
 * @create 2022/8/10 15:01
 * @description sdk config
 */
@Configuration
public class NcSdkConfigInfo {

    @Value("${chain.nodeRpcAddress}")
    private String nodeRpcAddress;
    @Value("${chain.chainId}")
    private long chainId;
    @Value("${chain.txPoolSleep}")
    private long txPoolSleep;

    @Autowired
    private WalletService walletService;

    @Bean
    public SpartanSdkClient getDDCSdkClient() {
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

}
