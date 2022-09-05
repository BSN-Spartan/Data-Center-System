package com.spartan.dc.model.vo.req;

import lombok.Data;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.math.BigInteger;

/**
 * @author wxq
 * @create 2022/8/24 16:51
 * @description
 */
@Data
public class MetaDataTxReqVO {
    @NotBlank(message = "nttWallet can not be empty")
    private String nttWallet;
    @Min(value = 1, message = "amount must be greater than 1 ")
    private String nttCount;
    private String metaTxSign;
    private String password;
    private String rechargeMetaId;
}
