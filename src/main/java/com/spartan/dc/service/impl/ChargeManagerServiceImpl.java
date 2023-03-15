package com.spartan.dc.service.impl;

import com.github.pagehelper.PageHelper;
import com.reddate.spartan.SpartanSdkClient;
import com.spartan.dc.core.datatables.DataTable;
import com.spartan.dc.core.enums.*;
import com.spartan.dc.core.exception.GlobalException;
import com.spartan.dc.core.util.bech32.Bech32Utils;
import com.spartan.dc.core.util.common.AddressUtils;
import com.spartan.dc.dao.write.ChainAccountRechargeMetaMapper;
import com.spartan.dc.dao.write.ChainPriceMapper;
import com.spartan.dc.dao.write.DcGasRechargeRecordMapper;
import com.spartan.dc.dao.write.SysDataCenterMapper;
import com.spartan.dc.model.ChainAccountRechargeMeta;
import com.spartan.dc.model.ChainPrice;
import com.spartan.dc.model.DcGasRechargeRecord;
import com.spartan.dc.model.SysDataCenter;
import com.spartan.dc.model.vo.req.MetaDataTxReqVO;
import com.spartan.dc.model.vo.resp.MetaDataTxRespVO;
import com.spartan.dc.model.vo.req.RechargeReqVO;
import com.spartan.dc.service.ChargeManagerService;
import org.apache.commons.lang3.StringUtils;
import org.fisco.bcos.web3j.crypto.WalletUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;
import org.web3j.utils.Strings;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author wxq
 * @create 2022/8/8 18:11
 * @description Charge manager service.
 */
@Service
public class ChargeManagerServiceImpl extends BaseService implements ChargeManagerService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String COSMOS_ADDRESS_PREFIX = "iaa";
    private static final int SCALE = 12;

    @Value("${metaTx.chainId}")
    public Integer chainId;
    @Value("${metaTx.deadline}")
    public Integer deadline;
    @Value("${metaTx.domainSeparator}")
    public String domainSeparator;
    @Value("${metaTx.metaTransferTypeHash}")
    public String metaTransferTypeHash;

    @Autowired
    private DcGasRechargeRecordMapper dcGasRechargeRecordMapper;


    @Autowired
    private ChainAccountRechargeMetaMapper rechargeMetaMapper;
    @Autowired
    private ChainPriceMapper chainPriceMapper;
    @Resource
    private SysDataCenterMapper sysDataCenterMapper;

    @Autowired
    private SpartanSdkClient spartanSdkClient;

    @Override
    public Map<String, Object> queryChargeList(DataTable<Map<String, Object>> dataTable) {

        PageHelper.startPage(dataTable.getParam().getPageIndex(), dataTable.getParam().getPageSize());

        Map<String, Object> condition = dataTable.getCondition();

        List<Map<String, Object>> list = dcGasRechargeRecordMapper.queryChargeList(condition);

        return dataTable.getReturnData(list);
    }


    @Override
    public boolean recharge(RechargeReqVO reqVO, Credentials credentials) throws Exception {

        checkDataCenterInfo(reqVO.getPassword(), credentials);

        String chargeChainAddress = reqVO.getChainAddress();

        if (Objects.equals(ChainTypeEnum.COSMOS.getCode().longValue(), reqVO.getChainId())) {
            if (reqVO.getChainAddress().startsWith("0x")) {
                // Ox transition iaa
                chargeChainAddress = Bech32Utils.hexToBech32(COSMOS_ADDRESS_PREFIX, chargeChainAddress.substring(2));
            }
        }

        // check address
        if (reqVO.getChainId() == ChainTypeEnum.COSMOS.getCode() && chargeChainAddress.startsWith(COSMOS_ADDRESS_PREFIX)) {
            AddressUtils.validAddress(chargeChainAddress);
        } else {
            if (!WalletUtils.isValidAddress(chargeChainAddress)) {
                throw new GlobalException("Address is illegal");
            }
        }

        SysDataCenter sysDataCenter = sysDataCenterMapper.getSysDataCenter();
        if (sysDataCenter == null) {
            throw new GlobalException("the basic information of the data center has not been configured yet");
        }
        try {
            String accOwner = spartanSdkClient.gasCreditRechargeService.getAccOwner(chargeChainAddress, BigInteger.valueOf(reqVO.getChainId()));
            if (StringUtils.isNotBlank(accOwner) && !sysDataCenter.getDcCode().equalsIgnoreCase(accOwner)) {
                throw new GlobalException("The wallet address has been bound by another data center");
            }
        } catch (Exception e) {
            logger.error("recharge error:", e);
            throw new GlobalException(e.getMessage());
        }

        DcGasRechargeRecord dcGasRechargeRecord = new DcGasRechargeRecord();
        BeanUtils.copyProperties(reqVO, dcGasRechargeRecord);
        dcGasRechargeRecord.setGas(reqVO.getGas());
        dcGasRechargeRecord.setState(RechargeSubmitStateEnum.PENDING_SUBMIT.getCode());
        dcGasRechargeRecord.setRechargeState(RechargeStateEnum.NO_PROCESSING_REQUIRED.getCode());
        dcGasRechargeRecord.setChainId((long) reqVO.getChainId());
        dcGasRechargeRecord.setAuditState(RechargeAuditStateEnum.AUDIT_HANDLE.getCode());
        dcGasRechargeRecord.setCreateUserId(getUserId());
        dcGasRechargeRecord.setCreateTime(new Date());
        dcGasRechargeRecord.setRechargeType(RechargeTypeEnum.PRESENTER.getCode());
        dcGasRechargeRecordMapper.insertSelective(dcGasRechargeRecord);
        return dcGasRechargeRecord.getRechargeRecordId() > 0;
    }

    @Override
    public MetaDataTxRespVO getMeatDataTxInfo(MetaDataTxReqVO reqVO, Credentials credentials) {
        if (Objects.isNull(chainId)) {
            throw new GlobalException("Please configure metaTx chainId");
        }
        if (Objects.isNull(deadline)) {
            throw new GlobalException("Please configure metaTx deadline");
        }
        if (Objects.isNull(domainSeparator)) {
            throw new GlobalException("Please configure metaTx domainSeparator");
        }
        if (Objects.isNull(domainSeparator)) {
            throw new GlobalException("Please configure metaTx metaTransferTypeHash");
        }

        SysDataCenter sysDataCenter = sysDataCenterMapper.getSysDataCenter();
        if (sysDataCenter == null) {
            throw new GlobalException("The basic information of the data center has not been configured yet");
        }

        // check data center
        checkDataCenterInfo(reqVO.getPassword(), credentials);

        Long localNonce = 1L;
        Long chainNonce = null;
        try {
            chainNonce = spartanSdkClient.gasCreditRechargeService.getNonce(sysDataCenter.getNttAccountAddress()).longValue();
        } catch (Exception e) {
            e.printStackTrace();
            throw new GlobalException(e.getMessage());
        }

        // Get the current and latest record
        ChainAccountRechargeMeta latestRecord = rechargeMetaMapper.getLatestRecord();
        if (latestRecord != null) {
            localNonce = latestRecord.getRechargeMetaId() + 1;
        }

        if (chainNonce > localNonce) {
            localNonce = chainNonce + 5;
        }

        long timestamp = System.currentTimeMillis() + (3600 * 1000 * deadline);

        // insert metaTx
        ChainAccountRechargeMeta metaTx = new ChainAccountRechargeMeta();
        metaTx.setRechargeMetaId(localNonce);
        metaTx.setDeadline(timestamp);
        metaTx.setGas(new BigDecimal(reqVO.getNttCount()));
        metaTx.setChainAccountAddress(reqVO.getNttWallet());
        rechargeMetaMapper.insertRechargeMetaRecord(metaTx);

        // return metaTx
        MetaDataTxRespVO resp = new MetaDataTxRespVO();
        resp.setDcAcc(reqVO.getNttWallet());
        resp.setReceiver(reqVO.getNttWallet());
        resp.setChainID(chainId);
        resp.setEngAmt(reqVO.getNttCount());
        resp.setNonce(localNonce.toString());
        resp.setDeadline(String.valueOf(timestamp));
        resp.setDomainSeparator(domainSeparator);
        resp.setMetaTransferTypeHash(metaTransferTypeHash);
        resp.setMateTxId(localNonce.toString());
        return resp;
    }

    @Override
    public String getMataTxSignature(MetaDataTxReqVO reqVO, Credentials credentials) {
        if (Strings.isEmpty(reqVO.getMetaTxSign())) {
            throw new GlobalException("Failed to get metaTxSign");
        }

        // check data center
        checkDataCenterInfo(reqVO.getPassword(), credentials);

        Sign.SignatureData signatureData = Sign.signMessage(Numeric.hexStringToByteArray(reqVO.getMetaTxSign()), credentials.getEcKeyPair(), false);
        String txSignature = Numeric.toHexStringNoPrefix(signatureData.getR()) + Numeric.toHexStringNoPrefix(signatureData.getS()) + Numeric.toHexStringNoPrefix(signatureData.getV());
        ChainAccountRechargeMeta rechargeMeta = new ChainAccountRechargeMeta();
        rechargeMeta.setRechargeMetaId(Long.valueOf(reqVO.getRechargeMetaId()));
        rechargeMeta.setSign(txSignature);
        rechargeMetaMapper.updateByPrimaryKeySelective(rechargeMeta);
        return txSignature;
    }

    @Override
    public String getChainPrice(Integer chainId) {
        ChainPrice chainPrice = chainPriceMapper.getOneByChainId(Long.parseLong(String.valueOf(chainId)));
        if (Objects.isNull(chainPrice)) {
            return null;
        }
        return chainPrice.getNttCount().toPlainString();
    }

}
