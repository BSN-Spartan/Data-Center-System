package com.spartan.dc.controller.dc.recharge;


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
@RequestMapping("price/")
public class PriceManagePageController {

    Logger logger = LoggerFactory.getLogger(PriceManagePageController.class);

    @Autowired
    private ChainPriceService chainPriceService;

//    @RequiredPermission(isPage = true)
    @RequestMapping(value = "getDetail/{salePriceId}")
    public String priceDetail(@PathVariable("salePriceId") Integer salePriceId) {
        logger.info("Go to price details page");
        // Check the basic information of the statement
        Map<String, Object> salePriceDetail = chainPriceService.getSalePriceDetail(salePriceId);

        if (salePriceDetail == null) {
            return "redirect:/price/list";
        }
        return "/price/detail";
    }

//    @RequiredPermission(isPage = true)
    @RequestMapping(value = "toAudit/{salePriceId}", method = RequestMethod.GET)
    public String auditPrice(@PathVariable("salePriceId") Integer salePriceId) {
        logger.info("Enter the price review page");
        // Check the basic information of the statement
        Map<String, Object> salePriceDetail = chainPriceService.getSalePriceDetail(salePriceId);

        if (salePriceDetail == null) {
            return "redirect:/price/list";
        }
        return "/price/audit";
    }



}