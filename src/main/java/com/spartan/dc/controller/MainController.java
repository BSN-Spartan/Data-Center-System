package com.spartan.dc.controller;

import com.spartan.dc.config.interceptor.RequiredPermission;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import springfox.documentation.annotations.ApiIgnore;


@Controller
@RequestMapping("/")
@ApiIgnore
public class MainController {

    @RequestMapping(value = "")
    public String main() {
        return "main/index";
    }

    @RequiredPermission(isPage = true)
    @RequestMapping(value = "index")
    public String index() {
        return "dashboard/dashboard";
    }

    @RequiredPermission(isPage = true)
    @RequestMapping(value = "user")
    public String user() {
        return "user/list";
    }

    @RequiredPermission(isPage = true)
    @RequestMapping(value = "node_reward")
    public String nodeReward() {
        return "node_reward/list";
    }

    @RequiredPermission(isPage = true)
    @RequestMapping(value = "gas_reward")
    public String gasReward() {
        return "gas_reward/list";
    }

    @RequiredPermission(isPage = true)
    @RequestMapping(value = "recharge")
    public String recharge() {
        return "recharge/list";
    }

    @RequiredPermission(isPage = true)
    @RequestMapping(value = "price")
    public String price() {
        return "price/list";
    }

    @RequiredPermission(isPage = true)
    @RequestMapping(value = "ntt")
    public String ntt() {
        return "ntt/list";
    }

    @RequiredPermission(isPage = true)
    @RequestMapping(value = "node")
    public String node() {
        return "node/list";
    }

    @RequiredPermission(isPage = true)
    @RequestMapping(value = "chain")
    public String chain() {
        return "chain/list";
    }

    @RequiredPermission(isPage = true)
    @RequestMapping(value = "modifyPass")
    public String modifyPass() {
        return "user/modifyPass";
    }

    @RequiredPermission(isPage = true)
    @RequestMapping(value = "/version")
    public String updatedInstructions() {
        return "index/version";
    }

    @RequiredPermission(isPage = true)
    @RequestMapping(value = "support")
    public String support() {
        return "support/support";
    }

    @RequiredPermission(isPage = true)
    @RequestMapping(value = "config")
    public String dashboard() {
        return "index/config";
    }

    @RequiredPermission(isPage = true)
    @RequestMapping(value = "financial")
    public String financial() {
        return "financial/orderList";
    }
    @RequiredPermission(isPage = true)
    @RequestMapping(value = "refund")
    public String refund() {
        return "financial/refundList";
    }
    @RequiredPermission(isPage = true)
    @RequestMapping(value = "orderListDetails")
    public String orderListDetails() {
        return "financial/orderListDetails";
    }

    @RequiredPermission(isPage = true)
    @RequestMapping("baseInfo")
    public String baseInfo() {
        return "setting/baseInfo";
    }

    @RequiredPermission(isPage = true)
    @RequestMapping("chainAccess")
    public String chainAccess() {
        return "setting/chainAccess";
    }

    @RequiredPermission(isPage = true)
    @RequestMapping("userAccess")
    public String userAccess() {
        return "access/list";
    }

    @RequiredPermission(isPage = true)
    @RequestMapping("technicalSupport")
    public String technicalSupport() {
        return "setting/technicalSupport";
    }

    @RequiredPermission(isPage = true)
    @RequestMapping("paymentType")
    public String paymentType() {
        return "setting/paymentType";
    }
}
