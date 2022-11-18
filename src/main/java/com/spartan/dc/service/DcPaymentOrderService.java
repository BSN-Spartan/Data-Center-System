package com.spartan.dc.service;

import com.spartan.dc.model.DcPaymentOrder;

/**
 * @author linzijun
 * @version V1.0
 * @date 2022/11/7 14:04
 */
public interface DcPaymentOrderService {

    DcPaymentOrder selectByPrimaryKey(Long orderId);

    int updateByPrimaryKeySelective(DcPaymentOrder record);

}
