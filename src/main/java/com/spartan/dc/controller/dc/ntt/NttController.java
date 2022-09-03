package com.spartan.dc.controller.dc.ntt;

import com.spartan.dc.config.interceptor.RequiredPermission;
import com.spartan.dc.core.datatables.DataTable;
import com.spartan.dc.core.dto.ResultInfo;
import com.spartan.dc.core.dto.ResultInfoUtil;
import com.spartan.dc.service.impl.ChargeManagerServiceImpl;
import com.spartan.dc.service.impl.NttServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author wxq
 * @create 2022/8/11 13:58
 * @description ntt controller
 */
@RestController
@RequestMapping("ntt/")
public class NttController {
    private final static Logger logger = LoggerFactory.getLogger(NttController.class);

    @Autowired
    private NttServiceImpl nttService;

    @RequiredPermission
    @PostMapping(value = "queryNttList")
    public ResultInfo queryChargeList(@RequestBody DataTable<Map<String, Object>> dataTable) {
        logger.info("load ntt list......");
        Map<String, Object> nttList = nttService.queryNttList(dataTable);
        return ResultInfoUtil.successResult(nttList);
    }
}
