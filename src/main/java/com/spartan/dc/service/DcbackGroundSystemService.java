package com.spartan.dc.service;

import com.spartan.dc.core.datatables.DataTable;
import com.spartan.dc.core.dto.ResultInfo;
import com.spartan.dc.core.vo.req.*;
import com.spartan.dc.core.vo.resp.DcPaymentOrderDetailsRespVO;
import com.spartan.dc.core.vo.resp.DcSystemConfRespVO;
import com.spartan.dc.model.DcPaymentType;
import com.spartan.dc.model.SysDataCenter;
import com.spartan.dc.model.vo.req.RemittanceRegisterReqVO;
import com.spartan.dc.model.vo.resp.DcPaymentOrderRespVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @ClassName DcbackGroundSystemService
 * @Author wjx
 * @Date 2022/11/3 20:15
 * @Version 1.0
 */
public interface DcbackGroundSystemService {
    ResultInfo updateDcSystemConf(MultipartFile file,List<DcSystemConfReqVO> dcSystemConfReqVO,HttpServletRequest request);

    Map<String, Object> queryDcPaymentOrder(DataTable<Map<String, Object>> dataTable);

    DcPaymentOrderDetailsRespVO queryDcPaymentOrderDetails(Long orderId);

    void exportDcPaymentOrder(DcPaymentOrderReqVO dcPaymentOrderReqVO, HttpServletResponse response);

    Map<String, Object> queryDcPaymentRefund(DataTable<Map<String, Object>> dataTable);

    void exportDcPaymentRefund(DcPaymentRefundReqVO dcPaymentOrderReqVO, HttpServletResponse response);

    ResultInfo remittanceRegistration(RemittanceRegisterReqVO dcPaymentRegistrationReqVO);

    ResultInfo refundProcessing(DcPaymentStartRefundReqVO dcPaymentStartRefundReqVO);

    ResultInfo updatePayCenter(List<DcPaymentTypeReqVO> dcPaymentTypeReqVOS);

    ResultInfo technicalSupport(List<DcSystemConfReqVO> dcPaymentTypeReqVOS);

    List<DcPaymentType> queryPayCenter();

    List<DcSystemConfRespVO> querySystemConf(HttpServletRequest request);

    List<DcPaymentOrderRespVO> onGoOrder();

    ResultInfo updateTreaty(SysDataCenterTreatyReqVO sysDataCenterTreatyReqVO);

    String queryTreaty(String treatyCode);
}
