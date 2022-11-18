package com.spartan.dc.controller.dc;

import com.spartan.dc.config.interceptor.RequiredPermission;
import com.spartan.dc.core.datatables.DataTable;
import com.spartan.dc.core.dto.ResultInfo;
import com.spartan.dc.core.dto.ResultInfoUtil;
import com.spartan.dc.service.DcChainAccessService;
import com.spartan.dc.service.DcChainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Map;


@RestController
@RequestMapping("sys/access/key")
@ApiIgnore
public class DcChainAccessController {

    @Autowired
    private DcChainAccessService dcChainAccessService;

    @RequiredPermission
    @PostMapping(value = "queryList")
    public ResultInfo queryList(@RequestBody DataTable<Map<String, Object>> dataTable) {

        Map<String, Object> map = dcChainAccessService.queryList(dataTable);

        return ResultInfoUtil.successResult(map);
    }

}
