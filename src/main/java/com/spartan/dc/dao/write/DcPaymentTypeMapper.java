package com.spartan.dc.dao.write;

import com.spartan.dc.core.vo.req.DcPaymentTypeReqVO;
import com.spartan.dc.core.vo.req.DcSystemConfReqVO;
import com.spartan.dc.model.DcPaymentType;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DcPaymentTypeMapper {
    int deleteByPrimaryKey(Long paymentTypeId);

    int insert(DcPaymentType record);

    int insertSelective(DcPaymentType record);

    DcPaymentType selectByPrimaryKey(Long paymentTypeId);

    int updateByPrimaryKeySelective(DcPaymentType record);

    int updateByPrimaryKey(DcPaymentType record);

    List<DcPaymentType> selectAllPaymentType();

    DcPaymentType selectPaymentByCode(String channelCode);

    void updatePayCenter(@Param("dcPaymentTypeReqList") List<DcPaymentTypeReqVO> dcPaymentTypeReqVOS);

    void technicalSupport(@Param("dcPaymentTypeReqVOS") List<DcSystemConfReqVO> dcPaymentTypeReqVOS);

    List<DcPaymentType> queryPayCenter();

    void updatePayEnableStatus(@Param("payEnableStatusList")  List<DcPaymentTypeReqVO> payEnableStatusList);
}