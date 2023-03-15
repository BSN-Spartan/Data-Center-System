package com.spartan.dc.controller.dc.setting;

import com.spartan.dc.core.dto.ResultInfo;
import com.spartan.dc.core.dto.ResultInfoUtil;
import com.spartan.dc.model.DcAccountBalanceConf;
import com.spartan.dc.service.DcAccountBalanceConfService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName Balance Monitor
 * @Author wjx
 * @Date 2022/11/7 19:03
 * @Version 1.0
 */
@RestController
@RequestMapping("monitor/")
@Api(tags = "Balance monitor interface", value = "Balance monitor interface")
@ApiIgnore
@Slf4j
public class BalanceMonitorController {


    @Resource
    private DcAccountBalanceConfService dcAccountBalanceConfService;

    @GetMapping(value = "list")
    @ApiOperation("Balance Monitor query")
    public ResultInfo<List<DcAccountBalanceConf>> queryAccountMonitor() {
        return ResultInfoUtil.successResult(dcAccountBalanceConfService.queryAccountMonitor());
    }

    @PostMapping(value = "update")
    @ApiOperation("Balance Monitor configuration")
    public ResultInfo updateAccountMonitor(@RequestBody List<DcAccountBalanceConf> DcAccountBalanceConfList) {
        return ResultInfoUtil.successResult(dcAccountBalanceConfService.updateAccountMonitor(DcAccountBalanceConfList));
    }

}
