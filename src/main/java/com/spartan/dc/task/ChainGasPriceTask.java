package com.spartan.dc.task;

import com.reddate.spartan.SpartanSdkClient;
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
 * Get Chain Gas Price
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
            LOG.info("Task Chain Gas Price fail: {}", "the basic information of data center is not configured");
            return;
        }

        if (!walletService.checkWalletExists()) {
            LOG.info("Task Chain Gas Price fail: {}", "the keystore information is not configured");
            return;
        }

        List<DcChain> dcChains = dcChainMapper.getAll();
        for (DcChain dcChain : dcChains) {

            BigDecimal price = spartanSdkClient.gasCreditRechargeService.getExchRatio(new BigInteger(dcChain.getChainId().toString()));
            if (price == null || BigDecimal.ZERO.compareTo(price) == 1) {
                continue;
            }

            ChainPrice chainPrice = chainPriceMapper.getOneByChainId(dcChain.getChainId());

            if (chainPrice == null) {
                chainPrice = new ChainPrice();
                chainPrice.setCreateTime(new Date());
                chainPrice.setGas(BigDecimal.ONE);
                chainPrice.setNttCount(price);
                chainPrice.setChainId(dcChain.getChainId());
                chainPriceMapper.insertSelective(chainPrice);
                continue;
            }

            chainPrice.setNttCount(price);
            chainPriceMapper.updateByPrimaryKeySelective(chainPrice);
        }

    }

}
