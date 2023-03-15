package com.spartan.dc.controller.dc.recharge;

import com.spartan.dc.config.interceptor.RequiredPermission;
import com.spartan.dc.controller.BaseController;
import com.spartan.dc.core.datatables.DataTable;
import com.spartan.dc.core.dto.ResultInfo;
import com.spartan.dc.core.dto.ResultInfoUtil;
import com.spartan.dc.core.enums.RechargeAuditStateEnum;
import com.spartan.dc.core.enums.RechargeStateEnum;
import com.spartan.dc.model.DcGasRechargeRecord;
import com.spartan.dc.model.vo.req.MetaDataTxReqVO;
import com.spartan.dc.model.vo.req.RechargeAuditReqVO;
import com.spartan.dc.model.vo.req.RechargeReqVO;
import com.spartan.dc.model.vo.resp.MetaDataTxRespVO;
import com.spartan.dc.model.vo.resp.RechargeDetailRespVO;
import com.spartan.dc.service.ChargeManagerService;
import com.spartan.dc.service.DcGasRechargeRecordService;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.web3j.crypto.Credentials;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Date;
import java.util.Map;



/**
 * @author wxq
 * @create 2022/8/8 18:09
 * @description charge manager
 */
@RestController
@RequestMapping("recharge/")
@ApiIgnore
public class RechargeManagerController extends BaseController {

    private final static Logger logger = LoggerFactory.getLogger(RechargeManagerController.class);

    @Autowired
    private ChargeManagerService chargeManagerService;

    @Autowired
    private DcGasRechargeRecordService dcGasRechargeRecordService;

    @RequiredPermission
    @PostMapping(value = "list")
    public ResultInfo queryChargeList(@RequestBody DataTable<Map<String, Object>> dataTable) {

        logger.info("load recharge list......");
        Map<String, Object> chargeList = chargeManagerService.queryChargeList(dataTable);
        return ResultInfoUtil.successResult(chargeList);
    }

    @RequiredPermission
    @PostMapping(value = "recharge")
    public ResultInfo recharge(@RequestBody @Validated RechargeReqVO vo) throws Exception {
        Credentials credentials = getCredentials(vo.getPassword());

        boolean result = chargeManagerService.recharge(vo, credentials);
        if (result) {
            return ResultInfoUtil.successResult("New success");
        } else {
            return ResultInfoUtil.errorResult("The new failure");
        }
    }

    @RequiredPermission
    @PostMapping(value = "audit")
    public ResultInfo rechargeAudit(@RequestBody @Validated RechargeAuditReqVO vo) throws Exception {
        DcGasRechargeRecord dcGasRechargeRecord = dcGasRechargeRecordService.selectByPrimaryKey(vo.getRechargeRecordId());
        if (dcGasRechargeRecord == null) {
            return ResultInfoUtil.errorResult("Record does not exist");
        }

        if (!dcGasRechargeRecord.getAuditState().equals(RechargeAuditStateEnum.AUDIT_HANDLE.getCode())) {
            return ResultInfoUtil.errorResult("Current status does not allow audit");
        }

        dcGasRechargeRecord.setAuditState(vo.getDecision());
        if(vo.getDecision().equals(RechargeAuditStateEnum.AUDIT_FAIL.getCode())){
            dcGasRechargeRecord.setRechargeState(RechargeStateEnum.FAILED.getCode());
            dcGasRechargeRecord.setRechargeTime(null);
        }
        dcGasRechargeRecord.setAuditTime(new Date());
        dcGasRechargeRecord.setAuditor(getUserId());
        dcGasRechargeRecord.setAuditRemarks(vo.getAuditRemarks());

        dcGasRechargeRecordService.updateByPrimaryKeySelective(dcGasRechargeRecord);

        return ResultInfoUtil.successResult("Success");
    }

    @RequiredPermission
    @RequestMapping(value = "detail/{rechargeRecordId}")
    public ResultInfo<RechargeDetailRespVO> rechargeDetail(@PathVariable("rechargeRecordId") Integer rechargeRecordId) throws Exception {
        RechargeDetailRespVO rechargeDetailRespVO = dcGasRechargeRecordService.rechargeDetail(Long.valueOf(rechargeRecordId));
        if (rechargeDetailRespVO == null) {
            return ResultInfoUtil.errorResult("Record does not exist");
        }
        return ResultInfoUtil.successResult(rechargeDetailRespVO);
    }

    @RequiredPermission
    @PostMapping(value = "checkPasswordExpired")
    public ResultInfo checkPasswordExpired() throws Exception {
        boolean result = chargeManagerService.checkPasswordExpired();
        if (result) {
            return ResultInfoUtil.successResult();
        } else {
            return ResultInfoUtil.errorResult("");
        }
    }

    @RequiredPermission
    @PostMapping(value = "metaDataTx")
    public ResultInfo metaDataTx(@RequestBody @Validated MetaDataTxReqVO vo) throws Exception {
        // check password
        Credentials credentials = getCredentials(vo.getPassword());
        MetaDataTxRespVO metaDataTxRespVO = chargeManagerService.getMeatDataTxInfo(vo, credentials);
        return ResultInfoUtil.successResult(metaDataTxRespVO);
    }


    @RequiredPermission
    @PostMapping(value = "metaTxSign")
    public ResultInfo metaTxSign(@RequestBody @Validated MetaDataTxReqVO vo) {
        Credentials credentials = getCredentials(vo.getPassword());
        String signatureData = chargeManagerService.getMataTxSignature(vo, credentials);
        return ResultInfoUtil.successResult(signatureData);
    }


    /**
     * @param chainId
     * @return
     */
    @RequiredPermission
    @PostMapping(value = "getChainPriceRatio/{chainId}")
    public ResultInfo getChainPrice(@PathVariable("chainId") Integer chainId) {
        String chainPrice = chargeManagerService.getChainPrice(chainId);
        if (Strings.isBlank(chainPrice)) {
            return ResultInfoUtil.errorResult("Failed to get Gas Credit exchange rate");
        }
        return ResultInfoUtil.successResult(chainPrice);
    }


}
