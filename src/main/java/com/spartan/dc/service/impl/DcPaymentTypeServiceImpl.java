package com.spartan.dc.service.impl;

import com.spartan.dc.core.vo.req.DcPaymentTypeReqVO;
import com.spartan.dc.core.vo.req.DcSystemConfReqVO;
import com.spartan.dc.dao.write.DcPaymentTypeMapper;
import com.spartan.dc.model.DcPaymentType;
import com.spartan.dc.service.DcPaymentTypeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author linzijun
 * @version V1.0
 * @date 2022/11/7 14:17
 */
@Service
public class DcPaymentTypeServiceImpl implements DcPaymentTypeService {

    @Resource
    private DcPaymentTypeMapper dcPaymentTypeMapper;

    @Override
    public DcPaymentType selectByPrimaryKey(Long paymentTypeId) {
        return dcPaymentTypeMapper.selectByPrimaryKey(paymentTypeId);
    }

    @Override
    public void updatePayCenter(List<DcPaymentTypeReqVO> dcPaymentTypeReqVOS) {
        dcPaymentTypeMapper.updatePayCenter(dcPaymentTypeReqVOS);
    }

    @Override
    public void technicalSupport(List<DcSystemConfReqVO> dcPaymentTypeReqVOS) {
        dcPaymentTypeMapper.technicalSupport(dcPaymentTypeReqVOS);
    }

    @Override
    public void updatePayEnableStatus(List<DcPaymentTypeReqVO> payEnableStatusList) {
        dcPaymentTypeMapper.updatePayEnableStatus(payEnableStatusList);
    }

}
