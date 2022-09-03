package com.spartan.dc.controller.dc;

import com.reddate.spartan.SpartanSdkClient;
import com.spartan.dc.config.interceptor.RequiredPermission;
import com.spartan.dc.core.conf.EventBlockConf;
import com.spartan.dc.core.dto.ResultInfo;
import com.spartan.dc.core.dto.ResultInfoUtil;
import com.spartan.dc.core.dto.dc.DataCenter;
import com.spartan.dc.dao.write.EventBlockMapper;
import com.spartan.dc.model.EventBlock;
import com.spartan.dc.model.vo.req.AddDataCenterReqVO;
import com.spartan.dc.service.SysDataCenterService;
import com.spartan.dc.service.WalletService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicLong;


@RestController
@RequestMapping("sys/dc/")
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

            // Get the latest block height
//            if (walletService.checkWalletExists()) {
//                BigInteger blockNumber = spartanSdkClient.baseService.getBlockNumber();
//                EventBlockConf.eventBlock = new AtomicLong(blockNumber.longValue());
//                EventBlock eventBlock = new EventBlock();
//                eventBlock.setBlockHeight(blockNumber.longValue());
//                eventBlockMapper.updateEventBlock(eventBlock);
//            }

            return ResultInfoUtil.successResult("successful");
        } else {
            return ResultInfoUtil.errorResult("failed");
        }
    }
}
