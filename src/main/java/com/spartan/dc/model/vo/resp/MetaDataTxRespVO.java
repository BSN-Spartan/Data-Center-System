package com.spartan.dc.model.vo.resp;

import lombok.Data;

import java.math.BigInteger;

/**
 * @author wxq
 * @create 2022/8/24 16:54
 * @description metadata tx resp
 */
@Data
public class MetaDataTxRespVO {
    private String dcAcc;
    private String receiver;
    private String engAmt;
    private Integer chainID;
    private BigInteger nonce;
    private String deadline;
    private String domainSeparator;
    private String metaTransferTypeHash;
    private BigInteger mateTxId;

}
