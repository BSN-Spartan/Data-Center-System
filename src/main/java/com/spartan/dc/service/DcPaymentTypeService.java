package com.spartan.dc.service;

import com.spartan.dc.core.vo.req.DcPaymentTypeReqVO;
import com.spartan.dc.core.vo.req.DcSystemConfReqVO;
import com.spartan.dc.model.DcPaymentType;

import java.util.List;

/**
 * @author linzijun
 * @version V1.0
 * @date 2022/11/7 14:16
 */
public interface DcPaymentTypeService {

    DcPaymentType selectByPrimaryKey(Long paymentTypeId);

    void updatePayCenter(List<DcPaymentTypeReqVO> dcPaymentTypeReqVOS);

    void technicalSupport(List<DcSystemConfReqVO> dcPaymentTypeReqVOS);

    void updatePayEnableStatus(List<DcPaymentTypeReqVO> payEnableStatusList);
}
