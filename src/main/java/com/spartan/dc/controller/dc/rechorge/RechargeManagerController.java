package com.spartan.dc.controller.dc.rechorge;

import com.spartan.dc.config.interceptor.RequiredPermission;
import com.spartan.dc.controller.BaseController;
import com.spartan.dc.core.datatables.DataTable;
import com.spartan.dc.core.dto.ResultInfo;
import com.spartan.dc.core.dto.ResultInfoUtil;
import com.spartan.dc.model.vo.req.MetaDataTxReqVO;
import com.spartan.dc.model.vo.req.RechargeReqVO;
import com.spartan.dc.model.vo.resp.MetaDataTxRespVO;
import com.spartan.dc.service.impl.ChargeManagerServiceImpl;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.web3j.crypto.Credentials;

import java.util.Map;



/**
 * @author wxq
 * @create 2022/8/8 18:09
 * @description charge manager
 */
@RestController
@RequestMapping("recharge/")
public class RechargeManagerController extends BaseController {

    private final static Logger logger = LoggerFactory.getLogger(RechargeManagerController.class);

    @Autowired
    private ChargeManagerServiceImpl chargeManagerService;


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
