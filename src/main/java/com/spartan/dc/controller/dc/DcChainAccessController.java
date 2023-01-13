package com.spartan.dc.controller.dc;

import com.spartan.dc.config.interceptor.RequiredPermission;
import com.spartan.dc.core.datatables.DataTable;
import com.spartan.dc.core.dto.ResultInfo;
import com.spartan.dc.core.dto.ResultInfoUtil;
import com.spartan.dc.core.vo.req.DcChainAccessReqVO;
import com.spartan.dc.service.DcChainAccessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
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

    @PostMapping("update/accessinformation")
    @ResponseBody
    public ResultInfo updateAccessInformation(@Validated @RequestBody DcChainAccessReqVO dcChainAccessReqVO) {
        return ResultInfoUtil.successResult(dcChainAccessService.updateAccessInformation(dcChainAccessReqVO));
    }

}
