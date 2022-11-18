package com.spartan.dc.dao.write;

import com.spartan.dc.model.DcPaymentRefund;

public interface DcPaymentRefundMapper {
    int deleteByPrimaryKey(Long refundId);

    int insert(DcPaymentRefund record);

    int insertSelective(DcPaymentRefund record);

    DcPaymentRefund selectByPrimaryKey(Long refundId);

    int updateByPrimaryKeySelective(DcPaymentRefund record);

    int updateByPrimaryKey(DcPaymentRefund record);

    DcPaymentRefund selectByOtherTradeNo(String otherTradeNo);
}