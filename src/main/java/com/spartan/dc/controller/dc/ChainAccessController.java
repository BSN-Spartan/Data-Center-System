package com.spartan.dc.controller.dc;

import com.alibaba.fastjson.JSON;
import com.spartan.dc.controller.BaseController;
import com.spartan.dc.core.conf.SystemConf;
import com.spartan.dc.core.dto.ResultInfo;
import com.spartan.dc.core.dto.ResultInfoUtil;
import com.spartan.dc.core.enums.DcChainAccessStateEnum;
import com.spartan.dc.core.enums.DcChainGatewayType;
import com.spartan.dc.core.enums.DcSystemConfTypeEnum;
import com.spartan.dc.core.enums.SystemConfCodeEnum;
import com.spartan.dc.core.exception.GlobalException;
import com.spartan.dc.core.util.common.UUIDUtil;
import com.spartan.dc.core.vo.resp.ChainAccessRespVO;
import com.spartan.dc.core.vo.resp.DcSystemConfRespVO;
import com.spartan.dc.dao.write.DcSystemConfMapper;
import com.spartan.dc.dao.write.SysDataCenterMapper;
import com.spartan.dc.model.DcChain;
import com.spartan.dc.model.DcChainAccess;
import com.spartan.dc.model.DcSystemConf;
import com.spartan.dc.model.SysDataCenter;
import com.spartan.dc.model.vo.req.AddChainAccessReqVO;
import com.spartan.dc.service.ChainAccessService;
import com.spartan.dc.service.DcChainAccessService;
import com.spartan.dc.service.DcChainService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author linzijun
 * @version V1.0
 * @date 2022/11/7 17:37
 */
@RestController
@RequestMapping("/chainAccess")
@ApiIgnore
@Api(tags = "Chain Access Settings", value = "Chain Access Settings")
public class ChainAccessController extends BaseController {

    @Autowired
    private ChainAccessService chainAccessService;

    @Autowired
    private DcChainService dcChainService;

    @Resource
    private DcSystemConfMapper dcSystemConfMapper;

    @PostMapping(value = "/add")
    @ApiOperation("submit")
    @Transactional
    public ResultInfo addChainAccess(@RequestBody @Valid String reqVO) {
        AddChainAccessReqVO addChainAccessReqVO = JSON.parseObject(reqVO, AddChainAccessReqVO.class);

        boolean result = chainAccessService.addChainAccess(addChainAccessReqVO);
        if (result) {
            return ResultInfoUtil.successResult("successful");
        } else {
            return ResultInfoUtil.errorResult("failed");
        }
    }

    @PostMapping(value = "/get")
    @ApiOperation("get")
    public ResultInfo<ChainAccessRespVO> getChainAccess() {
        ChainAccessRespVO chainAccessRespVO = new ChainAccessRespVO();


        // Get the data center configuration information table
        String tpdValue = dcSystemConfMapper.querySystemValue(DcSystemConfTypeEnum.CHAIN_INFORMATION_ACCESS.getCode(), SystemConfCodeEnum.TPD.getName());
        String tpsValue = dcSystemConfMapper.querySystemValue(DcSystemConfTypeEnum.CHAIN_INFORMATION_ACCESS.getCode(), SystemConfCodeEnum.TPS.getName());


        if(StringUtils.isNotBlank(tpdValue)){
            chainAccessRespVO.setTpd(Integer.valueOf(tpdValue));
        }
        if(StringUtils.isNotBlank(tpsValue)){
            chainAccessRespVO.setTps(Integer.valueOf(tpsValue));
        }

        Map<String,String> gateUrl = dcChainService.getGatewayUrl();


        chainAccessRespVO.setGatewayUrl(gateUrl.get("gatewayUrl"));
        chainAccessRespVO.setWsGatewayUrl(gateUrl.get("wsGatewayUrl"));
        chainAccessRespVO.setGrpcGatewayUrl(gateUrl.get("grpcGatewayUrl"));
        chainAccessRespVO.setNodeConfigs(dcChainService.getNodeConfigs());

        return ResultInfoUtil.successResult(chainAccessRespVO);
    }




}
