package com.spartan.dc.controller.dc;

import com.spartan.dc.config.interceptor.RequiredPermission;
import com.spartan.dc.controller.BaseController;
import com.spartan.dc.core.datatables.DataTable;
import com.spartan.dc.core.dto.ResultInfo;
import com.spartan.dc.core.dto.ResultInfoUtil;
import com.spartan.dc.model.vo.req.AddDcNodeReqVO;
import com.spartan.dc.model.vo.req.OffNetworkReqVO;
import com.spartan.dc.service.NodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.web3j.crypto.Credentials;

import javax.servlet.http.HttpSession;
import java.util.Map;


@RestController
@RequestMapping("node/")
public class DcNodeController extends BaseController {

    private final static Logger logger = LoggerFactory.getLogger(DcNodeController.class);

    @Autowired
    private NodeService nodeService;


    @RequiredPermission
    @PostMapping(value = "queryNodeList")
    public ResultInfo queryNodeList(@RequestBody DataTable<Map<String, Object>> dataTable) {

        logger.info("load node list......");
        Map<String, Object> chargeList = nodeService.queryNodeList(dataTable);
        return ResultInfoUtil.successResult(chargeList);
    }

    @RequiredPermission
    @PostMapping(value = "offNetwork")
    public ResultInfo offNetwork(@RequestBody @Validated OffNetworkReqVO vo, HttpSession session) {
        boolean result = nodeService.offNetwork(vo);
        if (result) {
            return ResultInfoUtil.successResult("OffNetwork success");
        } else {
            return ResultInfoUtil.errorResult("OffNetwork failed");
        }
    }


    @RequiredPermission
    @PostMapping("/addNode")
    @Transactional(transactionManager = "writeSqlTransactionManager")
    public ResultInfo addNode(@RequestBody @Validated AddDcNodeReqVO dcNode) throws Exception {
        logger.info("add node {}", dcNode);
        Credentials credentials = getCredentials(dcNode.getPassword());
        boolean result = nodeService.addNode(dcNode,credentials);
        if (result) {
            return ResultInfoUtil.successResult("addNode success");
        } else {
            return ResultInfoUtil.errorResult("addNode failed");
        }
    }


}
