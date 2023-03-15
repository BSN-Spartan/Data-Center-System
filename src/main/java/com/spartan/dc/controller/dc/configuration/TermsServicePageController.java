package com.spartan.dc.controller.dc.configuration;


import com.spartan.dc.config.interceptor.RequiredPermission;
import com.spartan.dc.service.ChainPriceService;
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
@RequestMapping("terms/")
public class TermsServicePageController {

    Logger logger = LoggerFactory.getLogger(TermsServicePageController.class);

    @Autowired
    private ChainPriceService chainPriceService;


    @RequiredPermission(isPage = true)
    @RequestMapping(value = "add")
    public String addTerms() {

        return "/terms/add";
    }

    @RequiredPermission(isPage = true)
    @RequestMapping(value = "getDetail/{serviceReordId}")
    public String termsDetail(@PathVariable("serviceReordId") Integer serviceReordId) {
        logger.info("Go to terms service details page");
        // Check the basic information of the statement
//        Map<String, Object> salePriceDetail = chainPriceService.getSalePriceDetail(salePriceId);

        if (serviceReordId == null) {
            return "redirect:/terms/list";
        }
        return "/terms/detail";
    }

    @RequiredPermission(isPage = true)
    @RequestMapping(value = "toAudit/{serviceRecordId}", method = RequestMethod.GET)
    public String auditTerms(@PathVariable("serviceRecordId") Integer serviceRecordId) {
        logger.info("Enter the price review page");
        // Check the basic information of the statement
//        Map<String, Object> salePriceDetail = chainPriceService.getSalePriceDetail(salePriceId);

        if (serviceRecordId == null) {
            return "redirect:/terms/list";
        }
        return "/terms/audit";
    }



}