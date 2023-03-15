package com.spartan.dc.handler;

import com.spartan.dc.core.enums.ChainGasRechargeRateEnum;

import java.math.BigDecimal;

/**
 * Desc：
 *
 * @Created by 2022-09-01 19:48
 */
public class ChainGasRechargeHandle {

    /**
     * @param chainId Chain ID
     * @param srcGas Original Gas Credit
     * @return java.math.BigDecimal
     * @desc：
     * @author Jimmy
     * @created by 2022/9/1 20:07
     */
    public static BigDecimal initGas(Long chainId, BigDecimal srcGas) {

        if (chainId == null || srcGas == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal rate = ChainGasRechargeRateEnum.getRate(chainId);
        if (rate != null) {
            return srcGas.multiply(rate);
        }
        return srcGas;
    }

    /**
     * @param chainId Chain ID
     * @param srcGas Original Gas Credit
     * @return java.math.BigDecimal
     * @desc：
     * @author Jimmy
     * @created by 2022/9/1 20:07
     */
    public static BigDecimal initGasDivide(Long chainId, BigDecimal srcGas) {

        if (chainId == null || srcGas == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal rate = ChainGasRechargeRateEnum.getRate(chainId);
        if (rate != null) {
            return srcGas.divide(rate);
        }
        return srcGas;
    }

}
