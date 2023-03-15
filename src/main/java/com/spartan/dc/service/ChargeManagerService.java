package com.spartan.dc.service;

import com.spartan.dc.core.datatables.DataTable;
import com.spartan.dc.model.vo.req.MetaDataTxReqVO;
import com.spartan.dc.model.vo.req.RechargeReqVO;
import com.spartan.dc.model.vo.resp.MetaDataTxRespVO;
import org.web3j.crypto.Credentials;

import java.util.Map;

/**
 * @author wxq
 * @create 2022/8/8 18:11
 * @description Charge manager service.
 */
public interface ChargeManagerService {

    /**
     * Query charge list
     *
     * @param dataTable
     * @return charge list
     */
    Map<String, Object> queryChargeList(DataTable<Map<String, Object>> dataTable);

    /**
     * recharge
     *
     * @param vo
     * @param credentials
     * @return
     * @throws Exception
     */
    boolean recharge(RechargeReqVO vo, Credentials credentials) throws Exception;

    /***
     * getMeatDataTxInfo
     * @param reqVO
     * @return
     */
    MetaDataTxRespVO getMeatDataTxInfo(MetaDataTxReqVO reqVO, Credentials cre);

    /***
     * getMataTxSignature
     * @param reqVO
     * @param cre
     * @return
     */
    String getMataTxSignature(MetaDataTxReqVO reqVO, Credentials cre);

    String getChainPrice(Integer chainId);

    boolean checkPasswordExpired();
}
