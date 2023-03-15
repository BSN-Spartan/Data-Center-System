package com.spartan.dc.controller.dc.configuration;

import com.spartan.dc.core.dto.ResultInfo;
import com.spartan.dc.core.dto.ResultInfoUtil;
import com.spartan.dc.core.enums.SystemConfCodeEnum;
import com.spartan.dc.core.vo.req.*;
import com.spartan.dc.core.vo.resp.DcSystemConfRespVO;
import com.spartan.dc.model.DcPaymentType;
import com.spartan.dc.model.vo.resp.DcPaymentOrderRespVO;
import com.spartan.dc.service.DcbackGroundSystemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * @ClassName PortalConfiguration
 * @Author wjx
 * @Date 2022/11/7 19:03
 * @Version 1.0
 */
@RestController
@RequestMapping("ground/portalconfiguration/")
@Api(tags = "Portal configuration interface", value = "Portal configuration interface")
@ApiIgnore
@Slf4j
public class PortalConfigurationController {

    @Autowired
    private DcbackGroundSystemService dcbackGroundSystemService;

    @PostMapping(value = "update/dcsystemconf")
    @ApiOperation("Portal parameters configuration logo")
    public ResultInfo updateDcSystemConf(@RequestPart(value = "file", required = false) MultipartFile file, @RequestPart("dcSystemConfReqVO") @Valid List<DcSystemConfReqVO> dcSystemConfReqVO, HttpServletRequest request) {
        return ResultInfoUtil.successResult(dcbackGroundSystemService.updateDcSystemConf(file,dcSystemConfReqVO,request));
    }

    @GetMapping(value = "query/paycenter")
    @ApiOperation("Payment method query")
    public ResultInfo<List<DcPaymentType>> queryPayCenter() {
        return ResultInfoUtil.successResult(dcbackGroundSystemService.queryPayCenter());
    }
    @PostMapping(value = "update/paycenter")
    @ApiOperation("Payment method configuration")
    public ResultInfo updatePayCenter(@RequestBody List<DcPaymentTypeReqVO> dcPaymentTypeReqVOS) {
        return ResultInfoUtil.successResult(dcbackGroundSystemService.updatePayCenter(dcPaymentTypeReqVOS));
    }
    @PostMapping(value = "update/technicalsupport")
    @ApiOperation("Technical support configuration")
    public ResultInfo technicalSupport(@RequestPart("dcSystemConfReqVOList") List<DcSystemConfReqVO> dcSystemConfReqVOList) {
        return ResultInfoUtil.successResult(dcbackGroundSystemService.technicalSupport(dcSystemConfReqVOList));
    }

    @GetMapping(value = "query/systemconf")
    @ApiOperation("System configuration parameter query (portal information, technical support, contact us)")
    public ResultInfo<List<DcSystemConfRespVO>> querySystemConf(HttpServletRequest request) {
        return ResultInfoUtil.successResult(dcbackGroundSystemService.querySystemConf(request));
    }

    @GetMapping(value = "query/ongoorder")
    @ApiOperation("Check the order in payment")
    public ResultInfo<List<DcPaymentOrderRespVO>> onGoOrder() {
        return ResultInfoUtil.successResult(dcbackGroundSystemService.onGoOrder());
    }

//    @PostMapping(value = "update/treaty")
//    @ApiOperation("Modify the terms of service")
//    public ResultInfo updateTreaty(@RequestBody SysDataCenterTreatyReqVO sysDataCenterTreatyReqVO) {
//        return ResultInfoUtil.successResult(dcbackGroundSystemService.updateTreaty(sysDataCenterTreatyReqVO));
//    }

    @GetMapping(value = "query/treaty")
    @ApiOperation("Query the terms of service")
    public ResultInfo<String> queryTreaty() {
        return ResultInfoUtil.successResult(dcbackGroundSystemService.queryTreaty(SystemConfCodeEnum.TREATY.getCode()));
    }

}
