package com.spartan.dc.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.reddate.spartan.SpartanSdkClient;
import com.reddate.spartan.dto.spartan.*;
import com.spartan.dc.core.conf.EventBlockConf;
import com.spartan.dc.core.util.common.Md5Utils;
import com.spartan.dc.core.util.enums.*;
import com.spartan.dc.dao.write.*;
import com.spartan.dc.model.*;
import com.spartan.dc.service.WalletService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;

/**
 * Event Analysis
 * @author linzijun
 * @version V1.0
 * @date 2022/8/15 17:02
 */
@Configuration
@ConditionalOnProperty(prefix = "task", name = "enabled", havingValue = "true")
public class EventAnalysisTask {

    private final static Logger LOG = LoggerFactory.getLogger(EventAnalysisTask.class);

    @Resource
    private DcGasRechargeRecordMapper dcGasRechargeRecordMapper;

    @Resource
    private SysDataCenterMapper sysDataCenterMapper;

    @Resource
    private NttTxRecordMapper nttTxRecordMapper;

    @Resource
    private SpartanSdkClient spartanSdkClient;

    @Resource
    private DcNodeMapper dcNodeMapper;

    @Resource
    private EventBlockMapper eventBlockMapper;

    @Resource
    private ChainPriceMapper chainPriceMapper;

    @Resource
    private NttTxSumMapper nttTxSumMapper;

    @Autowired
    private WalletService walletService;

    @Scheduled(cron = "${task.eventAnalytics}")
    private void configureTasks() throws Exception {

        LOG.info("Task event analysis Start");

        if (EventBlockConf.eventBlock == null) {
            LOG.info("Task event analysis fail: {}", "local event block is null");
            return;
        }

        SysDataCenter sysDataCenter = sysDataCenterMapper.getSysDataCenter();
        if (sysDataCenter == null) {
            LOG.info("Task event analysis fail: {}", "the basic information of data center is not configured");
            return;
        }

        if (!walletService.checkWalletExists()) {
            LOG.info("Task event analysis fail: {}", "the keystore information is not configured");
            return;
        }

        // Get Event Block
        BigInteger eventBlock = new BigInteger(EventBlockConf.eventBlock+"");
        BigInteger blockNumber = spartanSdkClient.baseService.getBlockNumber();
        LOG.info("Task event analysis Current Block: {}", eventBlock + " --- " + blockNumber);

        if (eventBlock.compareTo(blockNumber) == 1) {
            LOG.info("Block High Overflow");
            return;
        }

        // Event Analysis
        ArrayList<BaseEventBean> blockEvents;
        try {
            blockEvents = spartanSdkClient.blockEventService.getBlockEvent(eventBlock);
            LOG.info("Event Analysis Result: {}", JSON.toJSONString(blockEvents));
        } catch (Exception e) {
            LOG.info("Event Analysis Error");
            throw new RuntimeException(e.getMessage());
        }

        if (!CollectionUtils.isEmpty(blockEvents)) {
            blockEvents.forEach(baseEventBean -> {

                if (baseEventBean instanceof ECRechgEventBean) {

                    // Gas Recharge

                    ECRechgEventBean ecRechgEventBean = (ECRechgEventBean) baseEventBean;

                    if (!isCurrentDc(ecRechgEventBean.getTransactionInfoBean().getFrom(), ecRechgEventBean.getTransactionInfoBean().getTo(), sysDataCenter)) {
                        return;
                    }

                    // EventBean MD5
                    String md5Sign = Md5Utils.getMD5String(JSONObject.toJSONString(ecRechgEventBean));

                    // get dcGasRechargeRecord By md5Sign
                    DcGasRechargeRecord dcGasRechargeRecord = dcGasRechargeRecordMapper.getOneByMd5Sign(md5Sign);
                    if (dcGasRechargeRecord != null) {
                        LOG.info("Gas Recharge data already exist: {}", md5Sign);
                        return;
                    }

                    dcGasRechargeRecord = dcGasRechargeRecordMapper.getOneByTxHash(ecRechgEventBean.getTransactionInfoBean().getHash());
                    if (dcGasRechargeRecord == null) {
                        LOG.info("Gas Recharge tx hash already exist");
                        return;
                    }

                    dcGasRechargeRecord.setRechargeCode(ecRechgEventBean.getRechgID());
                    dcGasRechargeRecord.setNtt(ecRechgEventBean.getNttAmt());
                    dcGasRechargeRecord.setUpdateTime(new Date());
                    dcGasRechargeRecord.setState(RechargeSubmitStateEnum.SUBMITTED_SUCCESSFULLY.getCode());
                    dcGasRechargeRecord.setMd5Sign(md5Sign);
                    dcGasRechargeRecordMapper.updateByPrimaryKeySelective(dcGasRechargeRecord);

                } else if (baseEventBean instanceof MetaECRechgEventBean) {

                    // Meta Gas Recharge

                    MetaECRechgEventBean metaECRechgEventBean = (MetaECRechgEventBean) baseEventBean;

                    if (!isCurrentDc(metaECRechgEventBean.getTransactionInfoBean().getFrom(), metaECRechgEventBean.getTransactionInfoBean().getTo(), sysDataCenter)
                            && !sysDataCenter.getNttAccountAddress().equalsIgnoreCase(metaECRechgEventBean.getDcAcc())) {
                        return;
                    }

                    // EventBean MD5
                    String md5Sign = Md5Utils.getMD5String(JSONObject.toJSONString(metaECRechgEventBean));

                    // get dcGasRechargeRecord By md5Sign
                    DcGasRechargeRecord dcGasRechargeRecord = dcGasRechargeRecordMapper.getOneByMd5Sign(md5Sign);
                    if (dcGasRechargeRecord != null) {
                        LOG.info("Meta Gas Recharge data already exist: {}", md5Sign);
                        return;
                    }

                    dcGasRechargeRecord = new DcGasRechargeRecord();
                    dcGasRechargeRecord.setChainId(metaECRechgEventBean.getChainID().longValue());
                    dcGasRechargeRecord.setChainAddress(metaECRechgEventBean.getReceiver());
                    dcGasRechargeRecord.setGas(new BigDecimal(metaECRechgEventBean.getGcAmt()));
                    dcGasRechargeRecord.setRechargeTime(new Date());
                    dcGasRechargeRecord.setState(RechargeSubmitStateEnum.SUBMITTED_SUCCESSFULLY.getCode());
                    dcGasRechargeRecord.setRechargeState(RechargeStateEnum.SUCCESSFUL.getCode());
                    dcGasRechargeRecord.setTxHash(metaECRechgEventBean.getTransactionInfoBean().getHash());
                    dcGasRechargeRecord.setUpdateTime(new Date());
                    dcGasRechargeRecord.setRechargeCode(metaECRechgEventBean.getRechgID());
                    dcGasRechargeRecord.setNtt(metaECRechgEventBean.getNttAmt());
                    dcGasRechargeRecord.setRechargeResult(metaECRechgEventBean.getTransactionInfoBean().getHash());
                    dcGasRechargeRecord.setNonce(metaECRechgEventBean.getNonce().longValue());
                    dcGasRechargeRecord.setMd5Sign(md5Sign);
                    dcGasRechargeRecordMapper.insertSelective(dcGasRechargeRecord);

                } else if (baseEventBean instanceof TransferEventBean) {

                    // Ntt Tx

                    TransferEventBean transferEventBean = (TransferEventBean) baseEventBean;

                    if (!isCurrentDc(transferEventBean.getFrom(), transferEventBean.getTo(), sysDataCenter)
                            && !isCurrentDc(transferEventBean.getTransactionInfoBean().getFrom(), transferEventBean.getTo(), sysDataCenter)) {
                        return;
                    }

                    Short txType = transferEventBean.getTransType().shortValueExact();
                    BigDecimal amount = transferEventBean.getAmount();

                    // EventBean MD5
                    String md5Sign = Md5Utils.getMD5String(JSONObject.toJSONString(transferEventBean));

                    // get nttTxRecord By md5Sign
                    NttTxRecord nttTxRecord = nttTxRecordMapper.getOneByMd5SignAndTxType(md5Sign, txType);
                    if (nttTxRecord != null) {
                        LOG.info("Ntt Tx data already exist: {}", md5Sign);
                        return;
                    }

                    nttTxRecord = new NttTxRecord();
                    nttTxRecord.setTxHash(transferEventBean.getTransactionInfoBean().getHash());
                    nttTxRecord.setOperator(transferEventBean.getOperator());
                    nttTxRecord.setType(txType);
                    nttTxRecord.setFromAddress(transferEventBean.getFrom());
                    nttTxRecord.setToAddress(transferEventBean.getTo());
                    nttTxRecord.setNttCount(amount);
                    nttTxRecord.setFromNttBalance(transferEventBean.getFromBalance());
                    nttTxRecord.setToNttBalance(transferEventBean.getToBalance());
                    nttTxRecord.setCreateTime(new Date());
                    nttTxRecord.setTxTime(new Date(Long.valueOf(transferEventBean.getTimestamp())));
                    nttTxRecord.setMd5Sign(md5Sign);
                    nttTxRecordMapper.insertSelective(nttTxRecord);

                    // save ntt tx sum
                    boolean flowIn = transferEventBean.getTo().equalsIgnoreCase(sysDataCenter.getNttAccountAddress());
                    saveNttTxSum(flowIn, amount);

                } else if (baseEventBean instanceof MintTransferEventBean) {

                    // Mint Ntt Tx

                    MintTransferEventBean mintTransferEventBean = (MintTransferEventBean) baseEventBean;

                    if (!isCurrentDc(mintTransferEventBean.getFrom(), mintTransferEventBean.getTo(), sysDataCenter)
                            && !isCurrentDc(mintTransferEventBean.getTransactionInfoBean().getFrom(), mintTransferEventBean.getTransactionInfoBean().getTo(), sysDataCenter)) {
                        return;
                    }

                    Short txType = mintTransferEventBean.getTransType().shortValueExact();
                    BigDecimal amount = mintTransferEventBean.getAmount();

                    // EventBean MD5
                    String md5Sign = Md5Utils.getMD5String(JSONObject.toJSONString(mintTransferEventBean));

                    // get nttTxRecord By md5Sign
                    NttTxRecord nttTxRecord = nttTxRecordMapper.getOneByMd5SignAndTxType(md5Sign, txType);
                    if (nttTxRecord != null) {
                        LOG.info("Mint Ntt Tx data already exist: {}", md5Sign);
                        return;
                    }

                    nttTxRecord = new NttTxRecord();
                    nttTxRecord.setTxHash(mintTransferEventBean.getTransactionInfoBean().getHash());
                    nttTxRecord.setOperator(mintTransferEventBean.getOperator());
                    nttTxRecord.setType(txType);
                    nttTxRecord.setFromAddress(mintTransferEventBean.getFrom());
                    nttTxRecord.setToAddress(mintTransferEventBean.getTo());
                    nttTxRecord.setNttCount(amount);
                    nttTxRecord.setFromNttBalance(BigDecimal.ZERO);
                    nttTxRecord.setToNttBalance(mintTransferEventBean.getToBalance());
                    nttTxRecord.setCreateTime(new Date());
                    nttTxRecord.setTxTime(new Date(Long.valueOf(mintTransferEventBean.getTimestamp())));
                    nttTxRecord.setMd5Sign(md5Sign);
                    nttTxRecordMapper.insertSelective(nttTxRecord);

                    // save ntt tx sum
                    boolean flowIn = mintTransferEventBean.getTo().equalsIgnoreCase(sysDataCenter.getNttAccountAddress());
                    saveNttTxSum(flowIn, amount);

                } else if (baseEventBean instanceof EnterNetEventBean) {

                    // Node Net Work

                    EnterNetEventBean enterNetEventBean = (EnterNetEventBean)baseEventBean;

                    if (!enterNetEventBean.getDcID().equalsIgnoreCase(sysDataCenter.getDcCode())) {
                        return;
                    }

                    DcNode dcNode = dcNodeMapper.getOneByNodeID(enterNetEventBean.getNodeID());
                    if (dcNode == null) {
                        return;
                    }

                    dcNode.setState(NodeStateEnum.IN_THE_INSPECTION.getCode());
                    dcNode.setNttCount(enterNetEventBean.getRewardNTT());
                    dcNodeMapper.updateByPrimaryKeySelective(dcNode);

                } else if (baseEventBean instanceof UpdateNodeStatusEventBean) {

                    // Node net notice

                    UpdateNodeStatusEventBean updateNodeStatusEventBean = (UpdateNodeStatusEventBean)baseEventBean;

                    if (!updateNodeStatusEventBean.getDcID().equalsIgnoreCase(sysDataCenter.getDcCode())) {
                        return;
                    }

                    DcNode dcNode = dcNodeMapper.getOneByNodeID(updateNodeStatusEventBean.getNodeID());
                    if (dcNode == null) {
                        return;
                    }

                    Short status = updateNodeStatusEventBean.getStatus().shortValue();
                    if (status == 1) {
                        dcNode.setState(NodeStateEnum.NETWORK_SUCCESS.getCode());
                    } else if (status == 2) {
                        dcNode.setState(NodeStateEnum.NETWORK_FAIL.getCode());
                    }

                    dcNode.setApplyResultTxHash(updateNodeStatusEventBean.getTransactionInfoBean().getHash());
                    dcNode.setApplyResultTime(new Date());
                    dcNode.setReason(updateNodeStatusEventBean.getRemark());
                    dcNode.setNttCount(updateNodeStatusEventBean.getRewardNTT());

                    dcNodeMapper.updateByPrimaryKeySelective(dcNode);

                } else if (baseEventBean instanceof SetExchRadioEventBean) {

                    // Chain Gas Price

//                    SetExchRadioEventBean setExchRadioEventBean = (SetExchRadioEventBean)baseEventBean;
//
//                    ChainPrice chainPrice = new ChainPrice();
//                    chainPrice.setChainId(setExchRadioEventBean.getChainID().longValue());
//                    chainPrice.setGas(setExchRadioEventBean.getNttAmt());
//                    chainPrice.setCreateTime(new Date());
//
//                    chainPriceMapper.insertSelective(chainPrice);
                }

            });
        }

        // Increment Event Block
        EventBlockConf.eventBlock.incrementAndGet();
        eventBlockMapper.increment();

        LOG.info("Task event analysis End");
    }

    /**
     * If the current event does not belong to the current data center, the event ends
     * @param from
     * @param to
     * @param sysDataCenter
     * @return
     */
    public Boolean isCurrentDc(String from, String to, SysDataCenter sysDataCenter) {
        if (!from.equalsIgnoreCase(sysDataCenter.getNttAccountAddress())
                && !to.equalsIgnoreCase(sysDataCenter.getNttAccountAddress())) {
            return false;
        }
        return true;
    }

    /**
     * save ntt tx sum
     *
     * @param flowIn
     * @param amount
     */
    public void saveNttTxSum(boolean flowIn, BigDecimal amount) {
        NttTxSum nttTxSum = nttTxSumMapper.getOne();
        if (nttTxSum == null) {
            LOG.info("ntt tx sum is null");
            return;
        }
        if (flowIn) {
            nttTxSum.setFlowIn(nttTxSum.getFlowIn().add(amount));
        } else {
            nttTxSum.setFlowOut(nttTxSum.getFlowOut().add(amount));
        }
        nttTxSumMapper.updateByPrimaryKeySelective(nttTxSum);
    }

}
