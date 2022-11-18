package com.spartan.dc.controller.dc;

import com.reddate.spartan.SpartanSdkClient;
import com.spartan.dc.config.interceptor.RequiredPermission;
import com.spartan.dc.core.dto.ResultInfo;
import com.spartan.dc.core.dto.ResultInfoUtil;
import com.spartan.dc.core.dto.dc.DataCenter;
import com.spartan.dc.dao.write.EventBlockMapper;
import com.spartan.dc.model.vo.req.AddDataCenterReqVO;
import com.spartan.dc.service.SysDataCenterService;
import com.spartan.dc.service.WalletService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;


@RestController
@RequestMapping("sys/dc/")
@ApiIgnore
public class DataCenterController {

    private final static Logger logger = LoggerFactory.getLogger(DataCenterController.class);

    @Autowired
    private SysDataCenterService sysDataCenterService;

    @Resource
    private SpartanSdkClient spartanSdkClient;

    @Resource
    private EventBlockMapper eventBlockMapper;

    @Autowired
    private WalletService walletService;

    @RequiredPermission
    @GetMapping(value = "get")
    public ResultInfo get() {

        logger.info("Obtain data center data......");

        DataCenter dataCenter = sysDataCenterService.get();

        return ResultInfoUtil.successResult(dataCenter);
    }

    @RequiredPermission
    @PostMapping(value = "addDataCenter")
    public ResultInfo addDataCenter(@RequestBody @Validated AddDataCenterReqVO vo, HttpSession session) throws Exception {
        DataCenter dataCenter = sysDataCenterService.get();
        if (dataCenter != null) {
            return ResultInfoUtil.errorResult("Data already exists");
        }
        boolean result = sysDataCenterService.addDataCenter(vo);
        if (result) {
            return ResultInfoUtil.successResult("successful");
        } else {
            return ResultInfoUtil.errorResult("failed");
        }
    }
}
