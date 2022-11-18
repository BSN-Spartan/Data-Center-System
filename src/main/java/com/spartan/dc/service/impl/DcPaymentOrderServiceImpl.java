package com.spartan.dc.service.impl;

import com.spartan.dc.dao.write.DcPaymentOrderMapper;
import com.spartan.dc.model.DcPaymentOrder;
import com.spartan.dc.service.DcPaymentOrderService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author linzijun
 * @version V1.0
 * @date 2022/11/7 14:05
 */
@Service
public class DcPaymentOrderServiceImpl implements DcPaymentOrderService {

    @Resource
    private DcPaymentOrderMapper dcPaymentOrderMapper;


    @Override
    public DcPaymentOrder selectByPrimaryKey(Long orderId) {
        return dcPaymentOrderMapper.selectByPrimaryKey(orderId);
    }

    @Override
    public int updateByPrimaryKeySelective(DcPaymentOrder record) {
        return dcPaymentOrderMapper.updateByPrimaryKeySelective(record);
    }
}
