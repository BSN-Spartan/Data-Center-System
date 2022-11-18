package com.spartan.dc.dao.write;

import com.spartan.dc.core.vo.req.DcPaymentOrderReqVO;
import com.spartan.dc.core.vo.req.DcPaymentRefundReqVO;
import com.spartan.dc.core.vo.resp.DcPaymentOrderDetailsRespVO;
import com.spartan.dc.core.vo.resp.DcPaymentOrderExcelRespVO;
import com.spartan.dc.core.vo.resp.DcPaymentRefundExcelRespVO;
import com.spartan.dc.model.DcPaymentOrder;
import com.spartan.dc.model.vo.resp.DcPaymentOrderRespVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface DcPaymentOrderMapper {
    int deleteByPrimaryKey(Long orderId);

    int insert(DcPaymentOrder record);

    int insertSelective(DcPaymentOrder record);

    DcPaymentOrder selectByPrimaryKey(Long orderId);

    int updateByPrimaryKeySelective(DcPaymentOrder record);

    int updateByPrimaryKey(DcPaymentOrder record);

    List<Map<String, Object>> queryDcPaymentOrder(Map<String, Object> condition);

    DcPaymentOrderDetailsRespVO queryDcPaymentOrderDetails(Long orderId);

    List<DcPaymentOrderExcelRespVO> exportDcpaymentorder(DcPaymentOrderReqVO dcPaymentOrderReqVO);

    List<Map<String, Object>> queryDcPaymentRefund(Map<String, Object> condition);

    List<DcPaymentRefundExcelRespVO> exportDcPaymentRefund(DcPaymentRefundReqVO dcPaymentOrderReqVO);

    DcPaymentOrder selectOrderByTradeNo(String tradeNo);

    List<DcPaymentOrderRespVO> onGoOrder(@Param("stripe") Short stripe, @Param("coinbase") Short coinbase, @Param("payState") Short payState);
}