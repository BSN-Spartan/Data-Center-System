package com.spartan.dc.strategy;


import com.spartan.dc.delayed.entity.QueryPayResultEntity;
import com.spartan.dc.model.DcChainAccess;
import com.spartan.dc.model.vo.req.PaymentReqVO;
import com.spartan.dc.model.vo.req.RefundReqVO;
import com.spartan.dc.model.vo.resp.PaymentRespVO;
import com.spartan.dc.model.vo.resp.RefundRespVO;

/**
 * StrategyService:Define the policy interface
 * This is equivalent to abstracting a map from the class behind
 *
 * @author rjx
 * @date 2022/10/10 16:17
 */
public interface StrategyService {
    /**
     * Match the key of the policy [it is most reasonable for the key to use enumeration management].
     *
     * @return key
     */
    String fetchKey();

    /**
     * Generate orders
     */
    PaymentRespVO createOrder(PaymentReqVO paymentReqVO, DcChainAccess dcChainAccess);


    /**
     * refund
     * @param refundReqVO
     * @return
     */
    RefundRespVO refund(RefundReqVO refundReqVO,long currentUserId);

    /**e
     * Query the payment result
     * @param queryPayResultEntity
     * @return
     */
    boolean queryPayResult(QueryPayResultEntity queryPayResultEntity);

    boolean refundResult(QueryPayResultEntity queryPayResultEntity);

    /**
     * stripe Payment callback
     * @param json
     * @param reqSignature
     * @return
     */
    boolean stripeCallback(String json, String reqSignature);

    boolean coinbaseCallback(String json);

}