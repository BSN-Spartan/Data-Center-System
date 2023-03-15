package com.spartan.dc.controller.dc.recharge;


import com.spartan.dc.config.interceptor.RequiredPermission;
import com.spartan.dc.model.vo.resp.RechargeDetailRespVO;
import com.spartan.dc.service.ChainPriceService;
import com.spartan.dc.service.ChargeManagerService;
import com.spartan.dc.service.DcGasRechargeRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Map;


@Controller
@ApiIgnore
@RequestMapping("recharge/")
public class RechargeManagerPageController {

    Logger logger = LoggerFactory.getLogger(RechargeManagerPageController.class);

    @Autowired
    private DcGasRechargeRecordService dcGasRechargeRecordService;

    @RequiredPermission(isPage = true)
    @RequestMapping(value = "getDetail/{rechargeRecordId}")
    public String rechargeDetail(@PathVariable("rechargeRecordId") Integer rechargeRecordId) {
        logger.info("Go to recharge details page");
        // Check the basic information of the statement
        RechargeDetailRespVO rechargeDetail = dcGasRechargeRecordService.rechargeDetail(Long.valueOf(rechargeRecordId));

        if (rechargeDetail == null) {
            return "redirect:/recharge/list";
        }
        return "/recharge/detail";
    }

    @RequiredPermission(isPage = true)
    @RequestMapping(value = "toAudit/{rechargeRecordId}", method = RequestMethod.GET)
    public String auditRecharge(@PathVariable("rechargeRecordId") Integer rechargeRecordId) {
        logger.info("Enter the recharge review page");
        // Check the basic information of the statement
        RechargeDetailRespVO rechargeDetail = dcGasRechargeRecordService.rechargeDetail(Long.valueOf(rechargeRecordId));

        if (rechargeDetail == null) {
            return "redirect:/recharge/list";
        }
        return "/recharge/audit";
    }



}