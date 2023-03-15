package com.spartan.dc.controller.dc.configuration;

import com.spartan.dc.controller.BaseController;
import com.spartan.dc.core.datatables.DataTable;
import com.spartan.dc.core.dto.ResultInfo;
import com.spartan.dc.core.dto.ResultInfoUtil;
import com.spartan.dc.core.enums.RechargeAuditStateEnum;
import com.spartan.dc.core.enums.RechargeStateEnum;
import com.spartan.dc.core.enums.SystemConfCodeEnum;
import com.spartan.dc.core.enums.TermsServiceAuditStateEnum;
import com.spartan.dc.core.vo.req.DcSystemConfReqVO;
import com.spartan.dc.dao.write.DcSystemConfMapper;
import com.spartan.dc.dao.write.SysDataCenterMapper;
import com.spartan.dc.model.DcTermsServiceAudit;
import com.spartan.dc.model.SysDataCenter;
import com.spartan.dc.model.vo.req.AddTermsServiceReqVO;
import com.spartan.dc.model.vo.req.TermsServiceAuditReqVO;
import com.spartan.dc.model.vo.req.TermsServiceDetailReqVO;
import com.spartan.dc.model.vo.resp.TermsServiceDetailRespVO;
import com.spartan.dc.service.DcbackGroundSystemService;
import com.spartan.dc.service.TermsServiceAuditService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

/**
 * @author linzijun
 * @version V1.0
 * @date 2023/2/15 10:58
 */
@RestController
@RequestMapping("terms/")
public class TermsServiceController extends BaseController {

    @Autowired
    private TermsServiceAuditService termsServiceAuditService;

    @Autowired
    private SysDataCenterMapper sysDataCenterMapper;

    @Autowired
    private DcSystemConfMapper dcSystemConfMapper;

    @Autowired
    private DcbackGroundSystemService dcbackGroundSystemService;

    @GetMapping(value = "query/treaty")
    @ApiOperation("Query the terms of service")
    public ResultInfo<String> queryTreaty() {
        return ResultInfoUtil.successResult(dcbackGroundSystemService.queryTreaty(SystemConfCodeEnum.TREATY.getCode()));
    }

    @GetMapping(value = "query/treatyTemp")
    @ApiOperation("Query the terms of service")
    public ResultInfo<String> queryTreatyTemp() {
        return ResultInfoUtil.successResult(dcbackGroundSystemService.queryTreaty(SystemConfCodeEnum.TREATYTEMP.getCode()));
    }

    @GetMapping(value = "query/queryNewestTreaty")
    @ApiOperation("Query the least terms of service")
    public ResultInfo<String> queryNewestTreaty() {
        return ResultInfoUtil.successResult(termsServiceAuditService.queryNewestTreaty());
    }

    @PostMapping("add")
    public ResultInfo add(@RequestBody @Validated AddTermsServiceReqVO addTermsServiceReqVO) {

        // check audit exist
        if(termsServiceAuditService.checkUnreviewed()){
            return ResultInfoUtil.errorResult("There are agreements in \"Pending Review\" status, please do not repeatedly submit the request\"");
        }

        DcTermsServiceAudit dcTermsServiceAudit = new DcTermsServiceAudit();
        dcTermsServiceAudit.setTermsContent(addTermsServiceReqVO.getTermsContent());
        dcTermsServiceAudit.setCreator(getUserId());
        termsServiceAuditService.insertSelective(dcTermsServiceAudit);

        return ResultInfoUtil.successResult();
    }

    @PostMapping("checkUnreviewed")
    public ResultInfo<Boolean> checkUnreviewed() {
        return ResultInfoUtil.successResult(termsServiceAuditService.checkUnreviewed());
    }

    @PostMapping("list")
    public ResultInfo list(@RequestBody DataTable<Map<String, Object>> dataTable) {

        Map<String, Object> chargeList = termsServiceAuditService.queryTermsServiceAuditList(dataTable);

        return ResultInfoUtil.successResult(chargeList);
    }

    @PostMapping("audit")
    public ResultInfo audit(@RequestBody @Validated TermsServiceAuditReqVO termsServiceAuditReqVO) {

        SysDataCenter sysDataCenter = sysDataCenterMapper.getSysDataCenter();
        if (sysDataCenter == null) {
            return ResultInfoUtil.errorResult("basic information of data center has not been configured yet");
        }

        DcTermsServiceAudit dcTermsServiceAudit = termsServiceAuditService.selectByPrimaryKey(termsServiceAuditReqVO.getServiceAuditId());
        if (dcTermsServiceAudit == null) {
            return ResultInfoUtil.errorResult("record does not exist");
        }

        if (!dcTermsServiceAudit.getAuditState().equals(TermsServiceAuditStateEnum.AUDIT_HANDLE.getCode())) {
            return ResultInfoUtil.errorResult("Current status does not allow operation");
        }

        dcTermsServiceAudit.setAuditState(termsServiceAuditReqVO.getDecision());
        dcTermsServiceAudit.setRemark(termsServiceAuditReqVO.getRemark());
        dcTermsServiceAudit.setAuditTime(new Date());
        dcTermsServiceAudit.setAuditor(getUserId());

        termsServiceAuditService.updateByPrimaryKeySelective(dcTermsServiceAudit);

        if(termsServiceAuditReqVO.getDecision().equals(TermsServiceAuditStateEnum.AUDIT_SUCCESS.getCode())){
            DcSystemConfReqVO dcSystemConfReqVO = new DcSystemConfReqVO();
            dcSystemConfReqVO.setConfCode(SystemConfCodeEnum.TREATY.getCode());
            dcSystemConfReqVO.setConfValue(dcTermsServiceAudit.getTermsContent());
            ArrayList<DcSystemConfReqVO> dcSystemConfReqVOList = new ArrayList<>();
            dcSystemConfReqVOList.add(dcSystemConfReqVO);
            dcSystemConfMapper.updateDcSystemConf(dcSystemConfReqVOList);
        }

        return ResultInfoUtil.successResult();
    }

    @RequestMapping("detail/{serviceRecordId}")
    public ResultInfo<TermsServiceDetailRespVO> detail(@PathVariable("serviceRecordId") Integer serviceRecordId) {

        TermsServiceDetailRespVO termsServiceDetailRespVO = termsServiceAuditService.detail(Long.valueOf(serviceRecordId));

        return ResultInfoUtil.successResult(termsServiceDetailRespVO);
    }


}
