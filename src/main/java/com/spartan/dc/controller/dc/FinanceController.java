package com.spartan.dc.controller.dc;

import com.alibaba.fastjson.JSON;
import com.spartan.dc.controller.BaseController;
import com.spartan.dc.core.datatables.DataTable;
import com.spartan.dc.core.dto.ResultInfo;
import com.spartan.dc.core.dto.ResultInfoUtil;
import com.spartan.dc.core.vo.req.*;
import com.spartan.dc.core.vo.resp.DcPaymentOrderDetailsRespVO;
import com.spartan.dc.model.vo.req.RemittanceRegisterReqVO;
import com.spartan.dc.service.DcbackGroundSystemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Map;

/**
 * @ClassName DcbackGroundSystemController
 * @Author wjx
 * @Date 2022/11/3 20:02
 * @Version 1.0
 */

@RestController
@RequestMapping("ground/parameters/")
@Api(tags = "Backend financial interface", value = "Backend financial interface")
@ApiIgnore
public class FinanceController extends BaseController {

    @Autowired
    private DcbackGroundSystemService dcbackGroundSystemService;

    @PostMapping("query/dcpaymentorder")
    @ApiOperation(value = "Paging financial orders")
    public ResultInfo queryDcPaymentOrder(@RequestBody DataTable<Map<String, Object>> dataTable) {
        Map<String, Object> dcPaymentOrder = dcbackGroundSystemService.queryDcPaymentOrder(dataTable);
        return ResultInfoUtil.successResult(dcPaymentOrder);
    }

    @PostMapping("query/dcpaymentorder/details")
    @ApiOperation(value = "Query financial order details")
    public ResultInfo queryDcPaymentOrderDetails(@RequestBody DcPaymentOrderIdReqVO dcPaymentOrderIdReqVO) {
        DcPaymentOrderDetailsRespVO dcPaymentOrder = dcbackGroundSystemService.queryDcPaymentOrderDetails(dcPaymentOrderIdReqVO.getOrderId());
        return ResultInfoUtil.successResult(dcPaymentOrder);
    }

    @GetMapping(value = "export/dcpaymentorder")
    @ApiOperation(value = "Export financial order")
    public void getExportDcPaymentOrder(@Param("data") String data, HttpServletResponse response) {
        dcbackGroundSystemService.exportDcPaymentOrder(JSON.parseObject(data, DcPaymentOrderReqVO.class), response);
    }


    @PostMapping("query/dcpaymentrefund")
    @ApiOperation(value = "Paging financial refund orders")
    public ResultInfo queryDcPaymentRefund(@RequestBody DataTable<Map<String, Object>> dataTable) {
        Map<String, Object> dcPaymentOrder = dcbackGroundSystemService.queryDcPaymentRefund(dataTable);
        return ResultInfoUtil.successResult(dcPaymentOrder);
    }

    @GetMapping("export/dcpaymentrefund")
    @ApiOperation(value = "Export financial refund order")
    public void exportDcPaymentRefund(@Param("data") String data, HttpServletResponse response) {
        dcbackGroundSystemService.exportDcPaymentRefund(JSON.parseObject(data, DcPaymentRefundReqVO.class), response);
    }

    @PostMapping("remittance/registration")
    @ApiOperation(value = "Remittance registration")
    public ResultInfo remittanceRegistration(@RequestBody @Valid RemittanceRegisterReqVO remittanceRegisterReqVO) {
        return dcbackGroundSystemService.remittanceRegistration(remittanceRegisterReqVO);
    }

    @PostMapping("refund/processing")
    @ApiOperation(value = "Process refund")
    public ResultInfo refundProcessing(@RequestBody @Valid DcPaymentStartRefundReqVO dcPaymentStartRefundReqVO) {
        return ResultInfoUtil.successResult(dcbackGroundSystemService.refundProcessing(dcPaymentStartRefundReqVO));
    }
}
