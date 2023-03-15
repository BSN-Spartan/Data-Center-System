package com.spartan.dc.core.enums;


import java.math.BigDecimal;

/**
 * Desc：
 *
 * @Created by 2022-09-01 19:48
 */
public enum ChainGasRechargeRateEnum {
    ETH_(1L, new BigDecimal(1000000000)),
    POLYGON_(3L, new BigDecimal(1000000000)),
    /*
    COSMOS_(1L, new BigDecimal(1)),
    */
    ;


    private Long chainId;

    private BigDecimal rate;

    ChainGasRechargeRateEnum(Long chainId, BigDecimal rate) {
        this.chainId = chainId;
        this.rate = rate;
    }

    public static BigDecimal getRate(Long chainId) {
        if (chainId == null) {
            return null;
        }
        for (ChainGasRechargeRateEnum e : ChainGasRechargeRateEnum.values()) {
            if (e.chainId.equals(chainId)) {
                return e.rate;
            }
        }
        return null;
    }
}
