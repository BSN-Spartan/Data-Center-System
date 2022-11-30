package com.spartan.dc.service;

import com.spartan.dc.core.util.user.UserLoginInfo;
import com.spartan.dc.model.vo.req.*;
import com.spartan.dc.model.vo.resp.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Payment related service
 *
 * @Author : rjx
 * @Date : 2022/10/10 17:25
 **/
public interface PaymentService {

    PaymentRespVO createOrder(PaymentReqVO paymentReqVO, HttpServletRequest request);

    List<PaymentTypeRespVO> paymentType();

    CalcGasPriceRespVO calcGasPrice(CalcGasPriceReqVO calcGasPriceReqVO);

    CalcGasCreditRespVO calcGasCredit(CalcGasCreditReqVO calcGasCreditReqVO);

    GasExchangeRateRespVO gasExchangeRate(GasExchangeRateReqVO gasExchangeRateReqVO);

    RefundRespVO refund(RefundReqVO refundReqVO, long currentUserId);

    boolean queryPayResult(QueryPayResultReqVO queryPayResultReqVO);

    /**
     * stripe payment callback
     *
     * @param json
     * @param reqSignature
     */
    boolean stripeCallback(String json, String reqSignature);

    boolean coinbaseCallback(String json);
}
