package com.spartan.dc.controller.dc;

import com.spartan.dc.config.interceptor.RequiredPermission;
import com.spartan.dc.core.datatables.DataTable;
import com.spartan.dc.core.dto.ResultInfo;
import com.spartan.dc.core.dto.ResultInfoUtil;
import com.spartan.dc.core.vo.resp.DcChainRespVO;
import com.spartan.dc.model.DcChain;
import com.spartan.dc.service.DcChainService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("sys/chain/")
@ApiIgnore
public class DcChainController {

    private final static Logger logger = LoggerFactory.getLogger(DcChainController.class);

    @Autowired
    private DcChainService dcChainService;


    @RequiredPermission
    @PostMapping(value = "queryChainList")
    public ResultInfo queryChainList(@RequestBody DataTable<Map<String, Object>> dataTable) {

        logger.info("load chain list......");

        Map<String, Object> map = dcChainService.queryChainList(dataTable);

        return ResultInfoUtil.successResult(map);
    }

    @RequestMapping(value = "listOpbChain")
    @RequiredPermission
    public ResultInfo listOpbChain(){

        List<DcChain> list  = dcChainService.getOpbChainList();
        return ResultInfoUtil.successResult(list);
    }

//    @RequiredPermission
//    @GetMapping(value = "list")
//    public ResultInfo list(@RequestBody DataTable<Map<String, Object>> dataTable) {
//
//        logger.info("load node list......");
//
//        List<DcChain> dcChainList = dcChainService.queryNodeList(dataTable);
//
//        return ResultInfoUtil.successResult(dcChainList);
//    }



}
