package com.spartan.dc.controller.portal;

import com.spartan.dc.controller.dc.DcChainController;
import com.spartan.dc.core.dto.ResultInfo;
import com.spartan.dc.core.dto.ResultInfoUtil;
import com.spartan.dc.core.util.common.FileUtils;
import com.spartan.dc.core.vo.resp.DcChainRespVO;
import com.spartan.dc.core.vo.resp.DcSystemConfRespVO;
import com.spartan.dc.service.DcChainService;
import com.spartan.dc.service.PortalParametersService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @ClassName PortalParametersController
 * @Author wjx
 * @Date 2022/11/3 17:03
 * @Version 1.0
 */
@RestController
@RequestMapping("parameters/")
@Api(tags = "Portal-parameters interface", value = "Portal-parameters interface")
public class PortalParametersController {
    private final static Logger logger = LoggerFactory.getLogger(DcChainController.class);

    @Autowired
    private PortalParametersService portalParametersService;
    @Autowired
    private DcChainService dcChainService;

    @GetMapping(value = "query/systemconf")
    @ApiOperation("Portal-parameter information")
    public ResultInfo<List<DcSystemConfRespVO>> querySystemConf() {
        return ResultInfoUtil.successResult(portalParametersService.querySystemConf());
    }

    @GetMapping(value = "query/systemconftechnicalsupport")
    @ApiOperation("Portal-technical support")
    public ResultInfo<List<DcSystemConfRespVO>> querySystemConfTechnicalSupport() {
        return ResultInfoUtil.successResult(portalParametersService.querySystemConfTechnicalSupport());
    }

    @GetMapping(value = "query/systemconfcontactus")
    @ApiOperation("Portal-contact us")
    public ResultInfo<List<DcSystemConfRespVO>> querySystemConfContactUs() {
        return ResultInfoUtil.successResult(portalParametersService.querySystemConfContactUs());
    }

    @GetMapping(value = "query/chain")
    @ApiOperation("Get portal chain information")
    public ResultInfo<List<DcChainRespVO>> queryChain() {
        return ResultInfoUtil.successResult(dcChainService.queryChain());
    }



}
