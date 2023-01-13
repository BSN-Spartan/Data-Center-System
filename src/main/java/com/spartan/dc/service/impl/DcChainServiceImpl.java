package com.spartan.dc.service.impl;

import com.github.pagehelper.PageHelper;
import com.reddate.spartan.SpartanSdkClient;
import com.reddate.spartan.dto.spartan.ChainInfo;
import com.spartan.dc.core.datatables.DataTable;
import com.spartan.dc.core.vo.resp.ChainAccessRespVO;
import com.spartan.dc.core.vo.resp.DcChainRespVO;
import com.spartan.dc.dao.write.ChainPriceMapper;
import com.spartan.dc.dao.write.DcChainMapper;
import com.spartan.dc.dao.write.SysDataCenterMapper;
import com.spartan.dc.model.ChainPrice;
import com.spartan.dc.model.DcChain;
import com.spartan.dc.model.SysDataCenter;
import com.spartan.dc.service.DcChainService;
import com.spartan.dc.service.WalletService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Desc：
 *
 * @Created by 2022-07-16 20:29
 */
@Service
public class DcChainServiceImpl implements DcChainService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DcChainMapper dcChainMapper;
    @Resource
    private SysDataCenterMapper sysDataCenterMapper;

    @Resource
    private SpartanSdkClient spartanSdkClient;

    @Autowired
    private WalletService walletService;

    @Resource
    private ChainPriceMapper chainPriceMapper;


    @Override
    public Map<String, Object> queryChainList(DataTable<Map<String, Object>> dataTable) {

        PageHelper.startPage(dataTable.getParam().getPageIndex(), dataTable.getParam().getPageSize());

        List<Map<String, Object>> list = dcChainMapper.queryChainList(dataTable.getCondition());

        return dataTable.getReturnData(list);
    }

    @Override
    public DcChain selectByPrimaryKey(Long chainId) {
        return dcChainMapper.selectByPrimaryKey(chainId);
    }

    @Override
    public List<DcChain> getOpbChainList() {

        List<DcChain> list = dcChainMapper.getOpbChainList();

        return list;
    }

    @Override
    public List<DcChainRespVO> queryChain() {
        return dcChainMapper.queryChain();

    }

    @Override
    public int updateByPrimaryKeySelective(DcChain record) {
        return dcChainMapper.updateByPrimaryKeySelective(record);
    }

    /**
     * Reset node configuration
     * Preceding operations for node configuration
     */
    @Override
    public void resetNodeConfig() {
        dcChainMapper.resetNodeConfig();
    }

    /**
     * Get the configured gateway address
     * @return
     */
    @Override
    public Map<String, String> getGatewayUrl() {
        return dcChainMapper.getGatewayUrl();
    }

    /**
     * Get a list of node configurations
     * @return
     */
    @Override
    public List<ChainAccessRespVO.NodeConfig> getNodeConfigs() {
        return dcChainMapper.getNodeConfigs();
    }

    @Override
    public void queryChainPrice() throws Exception {
        SysDataCenter sysDataCenter = sysDataCenterMapper.getSysDataCenter();
        if (sysDataCenter == null) {
            logger.info("Task: Exception when querying the Gas Credit top-up ratio: {}", "the basic information of the data center has not been configured yet");
            return;
        }

        if (!walletService.checkWalletExists()) {
            logger.info("Task: Exception when querying the Gas Credit top-up ratio: {}", "the key store information has not been configured yet");
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
