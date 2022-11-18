package com.spartan.dc.controller.dc;

import com.spartan.dc.core.datatables.PageInfo;
import com.spartan.dc.core.dto.ResultInfo;
import com.spartan.dc.core.dto.ResultInfoUtil;
import com.spartan.dc.model.vo.req.GetBlockNumberReqVO;
import com.spartan.dc.model.vo.resp.NttBalanceRespVO;
import com.spartan.dc.model.vo.resp.NttRewardRespVO;
import com.spartan.dc.model.vo.resp.NttTxSumRespVO;
import com.spartan.dc.service.DashboardService;
import com.spartan.dc.service.NttService;
import com.spartan.dc.service.NttTxSumService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Map;
import java.util.Objects;

/**
 * @program: spartan_dc
 * @description: dashboard
 * @author: kuan
 * @create: 2022-08-20 14:55
 **/
@RestController
@RequestMapping("dashboard/")
@ApiIgnore
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private NttService nttService;

    @Autowired
    private NttTxSumService nttTxSumService;

    @PostMapping("getNttBalance/{address}")
    public ResultInfo getNttBalance(@PathVariable("address") String address) {
        NttBalanceRespVO ntt = dashboardService.getNttBalance(address);
        return ResultInfoUtil.successResult(ntt);
    }

    @GetMapping("getNttAddress")
    public ResultInfo getNttAddress() {
        String nttAddress = dashboardService.getNttAddress();
        return ResultInfoUtil.successResult(nttAddress);
    }

    @PostMapping("getGasBalance/{address}")
    public ResultInfo getGasBalance(@PathVariable("address") String address) {
        String gas = dashboardService.getGasBalance(address);
        return ResultInfoUtil.successResult(gas);
    }

    @PostMapping("getBlockNumber")
    public ResultInfo getBlockNumber(@RequestBody GetBlockNumberReqVO reqVO) {
        String blockNumber = dashboardService.getBlockNumber(reqVO);
        if (StringUtils.isEmpty(blockNumber)) {
            return ResultInfoUtil.errorResult("");
        }
        return ResultInfoUtil.successResult(blockNumber);
    }

    @PostMapping("listNodeInfo")
    public Map<String, Object> listNodeInfo(@RequestBody PageInfo pageInfo) {
        return dashboardService.listNodeInfo(pageInfo);
    }

    @PostMapping("listGasRecharge")
    public ResultInfo listGasRecharge() {
        Map<String, Object> chargeList = dashboardService.listGasRecharge();
        return ResultInfoUtil.successResult(chargeList);
    }


    @PostMapping("getNTTRecentAward")
    public ResultInfo getNTTRecentAward() {
        NttRewardRespVO nttTxRecord = nttService.getNTTRecentAward();
        if (Objects.isNull(nttTxRecord)) {
            nttTxRecord = new NttRewardRespVO();
            nttTxRecord.setNttCount("0.00");
            nttTxRecord.setTxTime("");
        }
        return ResultInfoUtil.successResult(nttTxRecord);
    }


    @PostMapping("getNTTDealsSummary")
    public ResultInfo getNTTDealsSummary() {
        NttTxSumRespVO nttTxSum = nttTxSumService.getNTTDealsSummary();
        return ResultInfoUtil.successResult(nttTxSum);
    }

}
