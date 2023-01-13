package com.spartan.dc.service.impl;

import com.github.pagehelper.PageHelper;
import com.spartan.dc.core.datatables.DataTable;
import com.spartan.dc.core.dto.ResultInfo;
import com.spartan.dc.core.dto.ResultInfoUtil;
import com.spartan.dc.core.easyexcel.DcPaymentOrderExcelEntity;
import com.spartan.dc.core.easyexcel.DcPaymentRefundExcelEntity;
import com.spartan.dc.core.enums.*;
import com.spartan.dc.core.exception.GlobalException;
import com.spartan.dc.core.util.bean.BeanUtils;
import com.spartan.dc.core.util.common.FileCode;
import com.spartan.dc.core.util.common.UUIDUtil;
import com.spartan.dc.core.util.excel.HutoolExcelUtils;
import com.spartan.dc.core.vo.req.*;
import com.spartan.dc.core.vo.resp.DcPaymentOrderDetailsRespVO;
import com.spartan.dc.core.vo.resp.DcPaymentOrderExcelRespVO;
import com.spartan.dc.core.vo.resp.DcPaymentRefundExcelRespVO;
import com.spartan.dc.core.vo.resp.DcSystemConfRespVO;
import com.spartan.dc.dao.write.*;
import com.spartan.dc.model.*;
import com.spartan.dc.model.vo.req.RemittanceRegisterReqVO;
import com.spartan.dc.model.vo.resp.DcPaymentOrderRespVO;
import com.spartan.dc.service.DcPaymentOrderService;
import com.spartan.dc.service.DcPaymentTypeService;
import com.spartan.dc.service.DcbackGroundSystemService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName DcbackGroundSystemServicelmpl
 * @Author wjx
 * @Date 2022/11/3 20:15
 * @Version 1.0
 */

@Service
@Slf4j
public class DcbackGroundSystemServicelmpl implements DcbackGroundSystemService {

    @Resource
    private DcSystemConfMapper dcSystemConfMapper;

    @Resource
    private DcPaymentOrderMapper dcPaymentOrderMapper;

    @Resource
    private DcPaymentRefundMapper dcPaymentRefundMapper;

    @Autowired
    private DcPaymentOrderService dcPaymentOrderService;

    @Autowired
    private DcPaymentTypeService dcPaymentTypeService;

    @Resource
    private DcGasRechargeRecordMapper dcGasRechargeRecordMapper;

    @Resource
    private DcPaymentTypeMapper dcPaymentTypeMapper;

    @Resource
    private SysDataCenterMapper sysDataCenterMapper;

    @Value("${system.iconBase64}")
    private String iconBase64;

    @Override
    public ResultInfo updateDcSystemConf(MultipartFile file, List<DcSystemConfReqVO> dcSystemConfReqVO, HttpServletRequest request) {
        if (file == null) {// No new logo uploaded/No need to change logo
            // Filter out everything except the logo
            List<DcSystemConfReqVO> dcSystemConfReqVOList = dcSystemConfReqVO.stream().filter(dc -> !dc.getConfCode().equals(SystemConfCodeEnum.LOGO.getCode())).collect(Collectors.toList());
            dcSystemConfMapper.updateDcSystemConf(dcSystemConfReqVOList);
            return ResultInfoUtil.successResult("Success");
        } else {// Uploading new logo requires deleting the original logo
            List<DcSystemConfReqVO> dcSystemConfReqVOList = dcSystemConfReqVO.stream().filter(dc -> !dc.getConfCode().equals(SystemConfCodeEnum.LOGO.getCode())).collect(Collectors.toList());
            dcSystemConfMapper.updateDcSystemConf(dcSystemConfReqVOList);

            SysDataCenter data = sysDataCenterMapper.getSysDataCenter();
            SysDataCenter sysDataCenter = new SysDataCenter();
            try {
                String base64 = FileCode.generateBase64(file);

                sysDataCenter.setDcId(data.getDcId());
                sysDataCenter.setLogo(base64);
                sysDataCenter.setCreateTime(new Date());
                sysDataCenterMapper.updateByPrimaryKeySelective(sysDataCenter);
            } catch (Exception e) {
                throw new GlobalException(e.getMessage());
            }
            return ResultInfoUtil.successResult("Success");
        }
    }

    @Override
    public Map<String, Object> queryDcPaymentOrder(DataTable<Map<String, Object>> dataTable) {
        PageHelper.startPage(dataTable.getParam().getPageIndex(), dataTable.getParam().getPageSize());
        List<Map<String, Object>> dcPaymentOrderRespVOS = dcPaymentOrderMapper.queryDcPaymentOrder(dataTable.getCondition());
        DecimalFormat format = new DecimalFormat("###,##0.00");
        for (Map<String, Object> dcPaymentOrderRespVO : dcPaymentOrderRespVOS) {
            dcPaymentOrderRespVO.put("payAmount", format.format(dcPaymentOrderRespVO.get("payAmount")));
        }
        return dataTable.getReturnData(dcPaymentOrderRespVOS);
    }

    @Override
    public DcPaymentOrderDetailsRespVO queryDcPaymentOrderDetails(Long orderId) {
        DcPaymentOrderDetailsRespVO dcPaymentOrderDetailsRespVO = dcPaymentOrderMapper.queryDcPaymentOrderDetails(orderId);
        DecimalFormat format = new DecimalFormat("###,##0.00");
        dcPaymentOrderDetailsRespVO.setCurrency(format.format(dcPaymentOrderDetailsRespVO.getPayAmount()) + " " + dcPaymentOrderDetailsRespVO.getCurrency());
        dcPaymentOrderDetailsRespVO.setGasCount(dcPaymentOrderDetailsRespVO.getGasCount() + " " + dcPaymentOrderDetailsRespVO.getRechargeUnit());
        return dcPaymentOrderDetailsRespVO;
    }

    @Override
    public void exportDcPaymentOrder(DcPaymentOrderReqVO dcPaymentOrderReqVO, HttpServletResponse response) {
        List<DcPaymentOrderExcelRespVO> dcPaymentOrderReqList = dcPaymentOrderMapper.exportDcpaymentorder(dcPaymentOrderReqVO);

        DecimalFormat format = new DecimalFormat("#0.00");
        for (DcPaymentOrderExcelRespVO dcPaymentOrderRespVO : dcPaymentOrderReqList) {
            format.format(dcPaymentOrderRespVO.getPayAmount());
        }
        List<DcPaymentOrderExcelEntity> paymentOrderExcelEntityList = new ArrayList<>();
        dcPaymentOrderReqList.forEach(memberPaymentOrderExtended -> {
            DcPaymentOrderExcelEntity paymentOrderExcelEntity = BeanUtils.convert(memberPaymentOrderExtended, DcPaymentOrderExcelEntity.class);
            paymentOrderExcelEntity.setPayType(memberPaymentOrderExtended.getPayType() == null ? "" : DcPaymentTypePayTypeEnum.getEnumByCode(memberPaymentOrderExtended.getPayType()).getName());
            paymentOrderExcelEntity.setPayState(memberPaymentOrderExtended.getPayState() == null ? "" : DcPaymentOrderPayStateEnum.getEnumByCode(memberPaymentOrderExtended.getPayState()).getName());
            paymentOrderExcelEntity.setGasRechargeState(memberPaymentOrderExtended.getGasRechargeState() == null ? "" : RechargeStateEnum.getEnumByCode(memberPaymentOrderExtended.getGasRechargeState()).getName());
            paymentOrderExcelEntity.setIsRefund(memberPaymentOrderExtended.getIsRefund() == null ? "" : DcPaymentOrderIsRefundEnum.getEnumByCode(memberPaymentOrderExtended.getIsRefund()).getName());
            paymentOrderExcelEntityList.add(paymentOrderExcelEntity);
        });
        if (CollectionUtils.isEmpty(paymentOrderExcelEntityList)) {
            paymentOrderExcelEntityList.add(new DcPaymentOrderExcelEntity());
        }
        HutoolExcelUtils.writeExcel("Order List", paymentOrderExcelEntityList, response);
    }

    @Override
    public Map<String, Object> queryDcPaymentRefund(DataTable<Map<String, Object>> dataTable) {
        PageHelper.startPage(dataTable.getParam().getPageIndex(), dataTable.getParam().getPageSize());
        List<Map<String, Object>> dcPaymentOrderRespVOS = dcPaymentOrderMapper.queryDcPaymentRefund(dataTable.getCondition());
        DecimalFormat format = new DecimalFormat("###,##0.00");
        for (Map<String, Object> dcPaymentOrderRespVO : dcPaymentOrderRespVOS) {
            dcPaymentOrderRespVO.put("payAmount", format.format(dcPaymentOrderRespVO.get("payAmount")));
        }
        return dataTable.getReturnData(dcPaymentOrderRespVOS);
    }

    @Override
    public void exportDcPaymentRefund(DcPaymentRefundReqVO dcPaymentOrderReqVO, HttpServletResponse response) {
        List<DcPaymentRefundExcelRespVO> dcPaymentRefundExcelRespVOList = dcPaymentOrderMapper.exportDcPaymentRefund(dcPaymentOrderReqVO);

        DecimalFormat format = new DecimalFormat("#0.00");
        for (DcPaymentRefundExcelRespVO dcPaymentOrderRespVO : dcPaymentRefundExcelRespVOList) {
            format.format(dcPaymentOrderRespVO.getPayAmount());
        }
        List<DcPaymentRefundExcelEntity> paymentRefundExcelEntityList = new ArrayList<>();
        dcPaymentRefundExcelRespVOList.forEach(memberPaymentOrderExtended -> {
            DcPaymentRefundExcelEntity paymentOrderExcelEntity = BeanUtils.convert(memberPaymentOrderExtended, DcPaymentRefundExcelEntity.class);
            paymentOrderExcelEntity.setPayType(memberPaymentOrderExtended.getPayType() == null ? "" : DcPaymentTypePayTypeEnum.getEnumByCode(memberPaymentOrderExtended.getPayType()).getName());
            paymentOrderExcelEntity.setRefundState(memberPaymentOrderExtended.getRefundState() == null ? "" : DcPaymentRefundRefundStateEnum.getEnumByCode(memberPaymentOrderExtended.getRefundState()).getName());
            paymentRefundExcelEntityList.add(paymentOrderExcelEntity);
        });
        if (CollectionUtils.isEmpty(paymentRefundExcelEntityList)) {
            paymentRefundExcelEntityList.add(new DcPaymentRefundExcelEntity());
        }
        HutoolExcelUtils.writeExcel("Refund List", paymentRefundExcelEntityList, response);
    }

    @Override
    public ResultInfo remittanceRegistration(RemittanceRegisterReqVO remittanceRegisterReqVO) {
        DcPaymentOrder dcPaymentOrder = dcPaymentOrderService.selectByPrimaryKey(remittanceRegisterReqVO.getOrderId());
        if (dcPaymentOrder == null) {
            return ResultInfoUtil.errorResult("Non-existent order records");
        }

        if (!dcPaymentOrder.getPayState().equals(DcPaymentOrderPayStateEnum.DURING_IN_PAYMENT.getCode())) {
            return ResultInfoUtil.errorResult("Current status does not allow operation");
        }

        DcPaymentType dcPaymentType = dcPaymentTypeService.selectByPrimaryKey(dcPaymentOrder.getPaymentTypeId());
        if (dcPaymentType == null) {
            return ResultInfoUtil.errorResult("Non-existent payment method");
        }

        if (!dcPaymentType.getPayType().equals(DcPaymentTypePayTypeEnum.OFFLINE.getCode())) {
            return ResultInfoUtil.errorResult("Non-remittance orders");
        }

        dcPaymentOrder.setOtherTradeNo(remittanceRegisterReqVO.getOtherTradeNo());
        dcPaymentOrder.setRemarks(remittanceRegisterReqVO.getRemarks());
        dcPaymentOrder.setPayState(DcPaymentOrderPayStateEnum.PAYMENT_SUCCESS.getCode());
        dcPaymentOrder.setGasRechargeState(RechargeStateEnum.PENDING.getCode());
        dcPaymentOrder.setUpdateTime(new Date());
        dcPaymentOrder.setPayTime(new Date());
        dcPaymentOrderService.updateByPrimaryKeySelective(dcPaymentOrder);

        DcGasRechargeRecord dcGasRecharge = dcGasRechargeRecordMapper.selectByOrderId(remittanceRegisterReqVO.getOrderId());
        if (Objects.isNull(dcGasRecharge)) {
            DcGasRechargeRecord dcGasRechargeRecord = new DcGasRechargeRecord();
            dcGasRechargeRecord.setChainId(dcPaymentOrder.getChainId());
            dcGasRechargeRecord.setOrderId(dcPaymentOrder.getOrderId());
            dcGasRechargeRecord.setChainAddress(dcPaymentOrder.getAccountAddress());
            dcGasRechargeRecord.setGas(dcPaymentOrder.getGasCount());
            dcGasRechargeRecord.setState(RechargeSubmitStateEnum.PENDING_SUBMIT.getCode());
            dcGasRechargeRecord.setTxHash("");
            dcGasRechargeRecord.setUpdateTime(new Date());
            dcGasRechargeRecord.setRechargeState(RechargeStateEnum.NO_PROCESSING_REQUIRED.getCode());
            dcGasRechargeRecordMapper.insertRechargeRecord(dcGasRechargeRecord);
        } else {
            return ResultInfoUtil.errorResult("Payment result query---The gas recharge record already exists");
        }


        return ResultInfoUtil.successResult("Success");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultInfo refundProcessing(DcPaymentStartRefundReqVO dcPaymentStartRefundReqVO) {
        DcPaymentOrder dcPaymentOrder = DcPaymentOrder.builder()
                .orderId(dcPaymentStartRefundReqVO.getOrderId())
                .isRefund(DcPaymentOrderIsRefundEnum.HAVEREFUND.getCode())
                .build();
        dcPaymentOrderMapper.updateByPrimaryKeySelective(dcPaymentOrder);
        DcPaymentOrder dcPay = dcPaymentOrderMapper.selectByPrimaryKey(dcPaymentStartRefundReqVO.getOrderId());

        DcPaymentRefund dcPaymentRefund = DcPaymentRefund.builder()
                .orderId(dcPaymentStartRefundReqVO.getOrderId())
                .tradeNo(CodePrefixEnum.SPARTANREFUND_ + UUIDUtil.generate())
                .accountAddress(dcPay.getAccountAddress())
                .refundAmount(dcPay.getPayAmount())
                .refundState(DcPaymentRefundRefundStateEnum.REFUND_SUCCESS.getCode())
                .refundTime(new Date())
                .remarks(dcPaymentStartRefundReqVO.getRemarks())
                .operator(dcPaymentStartRefundReqVO.getOperator())
                .createTime(new Date())
                .updateTime(new Date())
                .build();

        if (dcPaymentStartRefundReqVO.getPayType().equals(DcPaymentTypePayTypeEnum.OFFLINE.getCode())) {
            dcPaymentRefund.setOtherTradeNo(dcPaymentStartRefundReqVO.getOtherTradeNo());
            dcPaymentRefundMapper.insertSelective(dcPaymentRefund);
        } else if (dcPaymentStartRefundReqVO.getPayType().equals(DcPaymentTypePayTypeEnum.NCLT.getCode()) || dcPaymentStartRefundReqVO.getPayType().equals(DcPaymentTypePayTypeEnum.STABLECOIN.getCode())) {
            dcPaymentRefund.setOtherTradeNo(dcPay.getOtherTradeNo());
            dcPaymentRefundMapper.insertSelective(dcPaymentRefund);
        }
        return ResultInfoUtil.successResult("Success");

    }

    @Override
    public ResultInfo updatePayCenter(List<DcPaymentTypeReqVO> dcPaymentTypeReqVOS) {
//        List<DcPaymentTypeReqVO> dcSystemConfReqVOList = dcPaymentTypeReqVOS.stream()
//                .filter(dcPaymentTypeReqVO -> dcPaymentTypeReqVO.getEnableStatus().equals(DcPaymentTypeEnableStatusEnum.ENABLE.getCode()))
//                .collect(Collectors.toList());
//        List<DcPaymentTypeReqVO> payEnableStatusList = dcPaymentTypeReqVOS.stream()
//                .filter(dcPaymentTypeReqVO -> dcPaymentTypeReqVO.getEnableStatus().equals(DcPaymentTypeEnableStatusEnum.DISABLE.getCode()))
//                .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(dcPaymentTypeReqVOS)) {
            dcPaymentTypeService.updatePayCenter(dcPaymentTypeReqVOS);
        }
//        if (!CollectionUtils.isEmpty(payEnableStatusList)) {
//            dcPaymentTypeService.updatePayEnableStatus(payEnableStatusList);
//        }
        return ResultInfoUtil.successResult("Success");
    }

    @Override
    public ResultInfo technicalSupport(List<DcSystemConfReqVO> dcPaymentTypeReqVOS) {
        dcPaymentTypeService.technicalSupport(dcPaymentTypeReqVOS);
        return ResultInfoUtil.successResult("Success");
    }

    @Override
    public List<DcPaymentType> queryPayCenter() {
        return dcPaymentTypeMapper.queryPayCenter();
    }

    @Override
    public List<DcSystemConfRespVO> querySystemConf(HttpServletRequest request) {
        List<DcSystemConfRespVO> dcSystemConfRespVOS = dcSystemConfMapper.querySystemConf(null);
        DcSystemConfRespVO dcSystemConfRespVO = new DcSystemConfRespVO();
        SysDataCenter sysDataCenter = sysDataCenterMapper.getSysDataCenter();
        if (!Objects.isNull(sysDataCenter) && StringUtils.isNotBlank(sysDataCenter.getLogo())) {
            dcSystemConfRespVO.setConfCode(SystemConfCodeEnum.LOGO.getCode());
            dcSystemConfRespVO.setConfValue(sysDataCenter.getLogo());
            dcSystemConfRespVOS.add(dcSystemConfRespVO);
        } else {
            dcSystemConfRespVO.setConfCode(SystemConfCodeEnum.LOGO.getCode());
            dcSystemConfRespVO.setConfValue(iconBase64);
            dcSystemConfRespVOS.add(dcSystemConfRespVO);
        }
        return dcSystemConfRespVOS;
    }

    @Override
    public List<DcPaymentOrderRespVO> onGoOrder() {
        return dcPaymentOrderMapper.onGoOrder(DcPaymentTypePayTypeEnum.NCLT.getCode(), DcPaymentTypePayTypeEnum.STABLECOIN.getCode(), DcPaymentOrderPayStateEnum.DURING_IN_PAYMENT.getCode());
    }

    @Override
    public ResultInfo updateTreaty(SysDataCenterTreatyReqVO sysDataCenterTreatyReqVO) {
        SysDataCenter data = sysDataCenterMapper.getSysDataCenter();
        if (Objects.nonNull(data)) {
            DcSystemConfReqVO dcSystemConfReqVO = new DcSystemConfReqVO();
            dcSystemConfReqVO.setConfCode(SystemConfCodeEnum.TREATY.getCode());
            dcSystemConfReqVO.setConfValue(sysDataCenterTreatyReqVO.getTreaty());
            ArrayList<DcSystemConfReqVO> dcSystemConfReqVOList = new ArrayList<>();
            dcSystemConfReqVOList.add(dcSystemConfReqVO);
            dcSystemConfMapper.updateDcSystemConf(dcSystemConfReqVOList);


            return ResultInfoUtil.successResult("Success");
        }
        return ResultInfoUtil.errorResult("Data center information is not configured");
    }

    @Override
    public String queryTreaty() {
        SysDataCenter sysDataCenter = sysDataCenterMapper.getSysDataCenter();
        if (Objects.isNull(sysDataCenter)) {
            throw new GlobalException("Data center information is not configured");
        }
        List<DcSystemConfRespVO> dcSystemConfRespVOS = dcSystemConfMapper.querySystemConf(DcSystemConfTypeEnum.TREATY.getCode());
        if (dcSystemConfRespVOS.size()==0){
            throw new GlobalException("Terms of service content not initialized");
        }
        return dcSystemConfRespVOS.get(0).getConfValue();
    }
}
