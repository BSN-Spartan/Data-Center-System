package com.spartan.dc.task;

import com.reddate.spartan.SpartanSdkClient;
import com.reddate.spartan.dto.spartan.ChainInfo;
import com.spartan.dc.dao.write.ChainPriceMapper;
import com.spartan.dc.dao.write.DcChainMapper;
import com.spartan.dc.dao.write.SysDataCenterMapper;
import com.spartan.dc.model.ChainPrice;
import com.spartan.dc.model.DcChain;
import com.spartan.dc.model.SysDataCenter;
import com.spartan.dc.service.WalletService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

/**
 * Check the Gas Credit top-up ratio
 * @author linzijun
 * @version V1.0
 * @date 2022/8/30 19:12
 */
@Configuration
@ConditionalOnProperty(prefix = "task", name = "enabled", havingValue = "true")
public class ChainGasPriceTask {

    private final static Logger LOG = LoggerFactory.getLogger(ChainGasPriceTask.class);

    @Resource
    private ChainPriceMapper chainPriceMapper;

    @Resource
    private DcChainMapper dcChainMapper;

    @Resource
    private SysDataCenterMapper sysDataCenterMapper;

    @Autowired
    private WalletService walletService;

    @Resource
    private SpartanSdkClient spartanSdkClient;

    @Scheduled(cron = "${task.chainGasPrice}")
    private void configureTasks() throws Exception {

        SysDataCenter sysDataCenter = sysDataCenterMapper.getSysDataCenter();
        if (sysDataCenter == null) {
            LOG.info("Task: Exception when querying the Gas Credit top-up ratio: {}", "the basic information of the data center has not been configured yet");
            return;
        }

        if (!walletService.checkWalletExists()) {
            LOG.info("Task: Exception when querying the Gas Credit top-up ratio: {}", "the key store information has not been configured yet");
            return;
        }

        List<DcChain> dcChains = dcChainMapper.getAll();
        for (DcChain dcChain : dcChains) {
            getExchRatio(dcChain.getChainId());
            getChainInfo(dcChain.getChainId());
        }

    }

    private void getExchRatio(Long chainId) throws Exception {
        BigDecimal price = spartanSdkClient.gasCreditRechargeService.getExchRatio(new BigInteger(chainId.toString()));
        if (price == null || BigDecimal.ZERO.compareTo(price) == 1) {
            return;
        }
        saveChainPrice(chainId, price);
    }

    private void getChainInfo(Long chainId) throws Exception {
        ChainInfo chainInfo = spartanSdkClient.gasCreditRechargeService.getChainInfo(new BigInteger(chainId.toString()));
        if (chainInfo == null || chainInfo.getNttAmt() == null || BigDecimal.ZERO.compareTo(chainInfo.getNttAmt()) == 1) {
            return;
        }
        saveChainPrice(chainId, chainInfo.getNttAmt());
    }

    private void saveChainPrice(Long chainId, BigDecimal price) {
        ChainPrice chainPrice = chainPriceMapper.getOneByChainId(chainId);

        if (chainPrice == null) {
            chainPrice = new ChainPrice();
            chainPrice.setCreateTime(new Date());
            chainPrice.setGas(BigDecimal.ONE);
            chainPrice.setNttCount(price);
            chainPrice.setChainId(chainId);
            chainPriceMapper.insertSelective(chainPrice);
            return;
        }

        chainPrice.setNttCount(price);
        chainPriceMapper.updateByPrimaryKeySelective(chainPrice);
    }

}
