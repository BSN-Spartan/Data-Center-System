package com.spartan.dc.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.reddate.spartan.SpartanSdkClient;
import com.reddate.spartan.dto.spartan.*;
import com.spartan.dc.core.conf.EventBlockConf;
import com.spartan.dc.core.enums.NodeStateEnum;
import com.spartan.dc.core.enums.NttTxEnum;
import com.spartan.dc.core.enums.RechargeStateEnum;
import com.spartan.dc.core.enums.RechargeSubmitStateEnum;
import com.spartan.dc.core.util.common.Md5Utils;
import com.spartan.dc.dao.write.*;
import com.spartan.dc.model.*;
import com.spartan.dc.service.WalletService;
import org.apache.commons.lang.StringUtils;
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
import java.util.Objects;

/**
 * Event Analysis
 *
 * @author linzijun
 * @version V1.0
 * @date 2022/8/15 17:02
 */
@Configuration
@ConditionalOnProperty(prefix = "task", name = "enabled", havingValue = "true")
public class EventAnalysisTask {

    private final static Logger LOG = LoggerFactory.getLogger(EventAnalysisTask.class);

    private static String NTT_WALLET_ADDRESS = null;
    private static String DC_CODE = null;

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
    private NttTxSumMapper nttTxSumMapper;

    @Autowired
    private WalletService walletService;

    @Resource
    private ChainPriceMapper chainPriceMapper;

    @Scheduled(cron = "${task.eventAnalytics}")
    private void configureTasks() throws Exception {

        LOG.info("Task: event analysis start");

        if (EventBlockConf.eventBlock == null) {
            LOG.info("Task: Exception when parsing the event: {}", "the local block height is null");
            return;
        }

        if (!checkDataCenterInfo()) {
            return;
        }

        if (!walletService.checkWalletExists()) {
            LOG.info("Task: Exception when parsing the event: {}", "the key store information has not been configured yet");
            return;
        }

        // Get Event Block
        BigInteger eventBlock = new BigInteger(EventBlockConf.eventBlock.toString());
        BigInteger blockNumber = spartanSdkClient.baseService.getBlockNumber();
        LOG.info("Task: Parsing the block of the current block height by the event: {}", eventBlock + " --- " + blockNumber);

        if (eventBlock.compareTo(blockNumber) == 1) {
            LOG.info("Task: Current block height overflow");
            return;
        }

        // Event Analysis
        ArrayList<BaseEventBean> blockEvents;
        try {
            blockEvents = spartanSdkClient.blockEventService.getBlockEvent(eventBlock);
            LOG.info("Task: Event parsing result: {}", JSON.toJSONString(blockEvents));
            parseBlock(blockEvents);
        } catch (Exception e) {
            LOG.info("Task: Event parsing exception");
            throw new RuntimeException(e.getMessage());
        }

        // Increment Event Block
        EventBlockConf.eventBlock.incrementAndGet();
        eventBlockMapper.increment();

        LOG.info("Task: End of event parsing");
    }

    private void parseBlock(ArrayList<BaseEventBean> blockEvents) {
        if (!CollectionUtils.isEmpty(blockEvents)) {
            blockEvents.forEach(baseEventBean -> {
                if (baseEventBean instanceof ECRechgEventBean) {

                    // Gas Credit top-up
                    ECRechgEventBean ecRechgEventBean = (ECRechgEventBean) baseEventBean;
                    if (!isCurrentDc(ecRechgEventBean.getTransactionInfoBean().getFrom(), ecRechgEventBean.getTransactionInfoBean().getTo(), NTT_WALLET_ADDRESS)) {
                        return;
                    }

                    // Encrypt the event result with MD5 algorithm
                    String md5Sign = Md5Utils.getMD5String(JSONObject.toJSONString(ecRechgEventBean));

                    // Get the top-up record according to MD5Sign
                    DcGasRechargeRecord dcGasRechargeRecord = dcGasRechargeRecordMapper.getOneByMd5Sign(md5Sign);
                    if (dcGasRechargeRecord != null) {
                        LOG.info("Task: Gas Credit top-up data already exists: {}", md5Sign);
                        return;
                    }

                    dcGasRechargeRecord = dcGasRechargeRecordMapper.getOneByTxHash(ecRechgEventBean.getTransactionInfoBean().getHash());
                    if (dcGasRechargeRecord == null) {
                        LOG.info("Task: Gas Credit top-up hash already exists");
                        return;
                    }

                    dcGasRechargeRecord.setRechargeCode(ecRechgEventBean.getRechgID());
                    dcGasRechargeRecord.setNtt(ecRechgEventBean.getNttAmt());
                    dcGasRechargeRecord.setUpdateTime(new Date());
                    dcGasRechargeRecord.setState(RechargeSubmitStateEnum.SUBMITTED_SUCCESSFULLY.getCode());
                    dcGasRechargeRecord.setMd5Sign(md5Sign);
                    dcGasRechargeRecordMapper.updateByPrimaryKeySelective(dcGasRechargeRecord);
                    LOG.info("Task: Modify the top-up record based on the Gas Credit top-up event: {}", md5Sign);

                } else if (baseEventBean instanceof MetaECRechgEventBean) {

                    // Emergency Gas Credit top-up
                    MetaECRechgEventBean metaECRechgEventBean = (MetaECRechgEventBean) baseEventBean;
                    if (!isCurrentDc(metaECRechgEventBean.getTransactionInfoBean().getFrom(), metaECRechgEventBean.getTransactionInfoBean().getTo(), NTT_WALLET_ADDRESS)
                            && !NTT_WALLET_ADDRESS.equalsIgnoreCase(metaECRechgEventBean.getDcAcc())) {
                        return;
                    }

                    // Encrypt the event result with MD5 algorithm
                    String md5Sign = Md5Utils.getMD5String(JSONObject.toJSONString(metaECRechgEventBean));

                    // Get the top-up record according to MD5Sign
                    DcGasRechargeRecord dcGasRechargeRecord = dcGasRechargeRecordMapper.getOneByMd5Sign(md5Sign);
                    if (dcGasRechargeRecord != null) {
                        LOG.info("Task: The data of Emergency Gas Credit top-up already exists: {}", md5Sign);
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
                    LOG.info("Task: Add top-up record based on the Emergency Gas Credit top-up event: {}", md5Sign);

                } else if (baseEventBean instanceof TransferEventBean) {

                    // NTT transaction
                    TransferEventBean transferEventBean = (TransferEventBean) baseEventBean;
                    if (!isCurrentDc(transferEventBean.getFrom(), transferEventBean.getTo(), NTT_WALLET_ADDRESS)
                            && !isCurrentDc(transferEventBean.getTransactionInfoBean().getFrom(), transferEventBean.getTransactionInfoBean().getTo(), NTT_WALLET_ADDRESS)) {
                        return;
                    }

                    Short txType = transferEventBean.getTransType().shortValueExact();
                    BigDecimal amount = transferEventBean.getAmount();

                    // Encrypt the event result with MD5 algorithm
                    String md5Sign = Md5Utils.getMD5String(JSONObject.toJSONString(transferEventBean));

                    // Get NTT transaction record based on MD5Sign
                    NttTxRecord nttTxRecord = nttTxRecordMapper.getOneByMd5SignAndTxType(md5Sign, txType);
                    if (nttTxRecord != null) {
                        LOG.info("Task: NTT transaction record data already exists: {}", md5Sign);
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
                    LOG.info("Task: Add NTT transaction record based on the NTT transaction event: {}, Transaction type: {}", md5Sign, txType);
                    // Save NTT transaction cost statistics
                    boolean flowIn = transferEventBean.getTo().equalsIgnoreCase(NTT_WALLET_ADDRESS);
                    saveNttTxSum(flowIn, amount, txType);

                } else if (baseEventBean instanceof MintTransferEventBean) {

                    // Transaction of NTT generation
                    MintTransferEventBean mintTransferEventBean = (MintTransferEventBean) baseEventBean;
                    if (!isCurrentDc(mintTransferEventBean.getFrom(), mintTransferEventBean.getTo(), NTT_WALLET_ADDRESS)
                            && !isCurrentDc(mintTransferEventBean.getTransactionInfoBean().getFrom(), mintTransferEventBean.getTransactionInfoBean().getTo(), NTT_WALLET_ADDRESS)) {
                        return;
                    }

                    Short txType = mintTransferEventBean.getTransType().shortValueExact();
                    BigDecimal amount = mintTransferEventBean.getAmount();

                    // Encrypt the event result with MD5 algorithm
                    String md5Sign = Md5Utils.getMD5String(JSONObject.toJSONString(mintTransferEventBean));

                    // Get NTT transaction record based on MD5Sign
                    NttTxRecord nttTxRecord = nttTxRecordMapper.getOneByMd5SignAndTxType(md5Sign, txType);
                    if (nttTxRecord != null) {
                        LOG.info("Task: The transaction data of NTT generation already exists: {}", md5Sign);
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
                    LOG.info("Task: Add NTT transaction record based on the transaction event of NTT generation: {}, Transaction type: {}", md5Sign, txType);
                    // Save NTT transaction cost statistics
                    boolean flowIn = mintTransferEventBean.getTo().equalsIgnoreCase(NTT_WALLET_ADDRESS);
                    saveNttTxSum(flowIn, amount, txType);

                } else if (baseEventBean instanceof EnterNetEventBean) {

                    // Node registration
                    EnterNetEventBean enterNetEventBean = (EnterNetEventBean) baseEventBean;
                    if (!enterNetEventBean.getDcID().equalsIgnoreCase(DC_CODE)) {
                        return;
                    }

                    DcNode dcNode = dcNodeMapper.getOneByNodeID(enterNetEventBean.getNodeID());
                    if (dcNode == null) {
                        LOG.info("Task: Unable to find the node ID in the database, the current block height is: {}", enterNetEventBean.getNodeID());
                        return;
                    }

                    dcNode.setState(NodeStateEnum.IN_THE_INSPECTION.getCode());
                    dcNode.setNttCount(enterNetEventBean.getRewardNTT());
                    dcNodeMapper.updateByPrimaryKeySelective(dcNode);
                    LOG.info("Task: Modify the node status according to the node ID of the node registration event:{}", dcNode.getNodeId());

                } else if (baseEventBean instanceof UpdateNodeStatusEventBean) {

                    // Node registration notification
                    UpdateNodeStatusEventBean updateNodeStatusEventBean = (UpdateNodeStatusEventBean) baseEventBean;
                    if (!updateNodeStatusEventBean.getDcID().equalsIgnoreCase(DC_CODE)) {
                        return;
                    }

                    DcNode dcNode = dcNodeMapper.getOneByNodeID(updateNodeStatusEventBean.getNodeID());
                    if (dcNode == null) {
                        LOG.info("Task: Unable to find the node ID in the database, the current block height is: {}", updateNodeStatusEventBean.getNodeID(), updateNodeStatusEventBean.getBlockNumber());
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
                    LOG.info("Task: Modify node status according to the node ID of node registration notification event: {}", dcNode.getNodeId());
                } else if (baseEventBean instanceof ChainInfoEventBean) {

                    // chain info
                    ChainInfoEventBean chainInfoEventBean = (ChainInfoEventBean) baseEventBean;

                    if (chainInfoEventBean == null || BigDecimal.ZERO.compareTo(chainInfoEventBean.getNttAmt()) == 1) {
                        return;
                    }

                    ChainPrice chainPrice = chainPriceMapper.getOneByChainId(chainInfoEventBean.getChainID().longValue());

                    if (chainPrice == null) {
                        chainPrice = new ChainPrice();
                        chainPrice.setCreateTime(new Date());
                        chainPrice.setGas(BigDecimal.ONE);
                        chainPrice.setNttCount(chainInfoEventBean.getNttAmt());
                        chainPrice.setChainId(chainInfoEventBean.getChainID().longValue());
                        chainPriceMapper.insertSelective(chainPrice);
                        return;
                    }

                    chainPrice.setNttCount(chainInfoEventBean.getNttAmt());
                    chainPriceMapper.updateByPrimaryKeySelective(chainPrice);

                }
            });
        }
    }

    private boolean checkDataCenterInfo() {
        if (StringUtils.isBlank(NTT_WALLET_ADDRESS) || StringUtils.isBlank(DC_CODE)) {
            SysDataCenter sysDataCenter = sysDataCenterMapper.getSysDataCenter();
            if (sysDataCenter == null) {
                LOG.info("Task: Event parsing exception: {}", "basic information of data center has not been configured yet");
                return false;
            }
            NTT_WALLET_ADDRESS = sysDataCenter.getNttAccountAddress();
            DC_CODE = sysDataCenter.getDcCode();
        }
        return true;
    }

    /**
     * If the current event does not belong to the current data center, end the event
     *
     * @param from
     * @param to
     * @param nttWallet
     * @return
     */
    public Boolean isCurrentDc(String from, String to, String nttWallet) {
        if (!from.equalsIgnoreCase(nttWallet) && !to.equalsIgnoreCase(nttWallet)) {
            return false;
        }
        return true;
    }

    /**
     * Save NTT transaction cost statistics
     *
     * @param flowIn
     * @param amount
     */
    public void saveNttTxSum(boolean flowIn, BigDecimal amount, Short txType) {
        NttTxSum nttTxSum = nttTxSumMapper.getOne();
        if (Objects.isNull(nttTxSum)) {
            LOG.error("ntt tx sum is null");
            return;
        }

        // Process the refund of failed Gas Credit top-up in priority
        if (NttTxEnum.GAS_RECHARGE_FAIL_REFUND.getCode().equals(txType)) {
            nttTxSum.setFlowOut(nttTxSum.getFlowOut().subtract(amount));
            nttTxSumMapper.updateByPrimaryKeySelective(nttTxSum);
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
