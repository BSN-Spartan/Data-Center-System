package com.spartan.dc.controller.dc.configuration;

import com.spartan.dc.core.dto.ResultInfo;
import com.spartan.dc.core.dto.ResultInfoUtil;
import com.spartan.dc.model.DcMailConf;
import com.spartan.dc.model.vo.req.DcMailConfReqVO;
import com.spartan.dc.service.MailConfigurationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;


/**
 * @ClassName MailConfigurationController
 * @Author wjx
 * @Date 2023/3/7 10:34
 * @Version 1.0
 */
@RestController
@RequestMapping("ground/mail/")
@Api(tags = "Email Settings", value = "Email Settings")
@ApiIgnore
@Slf4j
public class MailConfigurationController {
    @Autowired
    private MailConfigurationService mailConfigurationService;

    @PostMapping(value = "send/test")
    @ApiOperation("Test email sending function")
    public ResultInfo sendTest(@RequestBody DcMailConfReqVO dcMailConfReqVO) {
        return ResultInfoUtil.successResult(mailConfigurationService.sendTest(dcMailConfReqVO));
    }

    @PostMapping(value = "update/send")
    @ApiOperation("Change sending configuration")
    public ResultInfo updateSendMode(@RequestBody List<DcMailConfReqVO> dcMailConfReqVOs) throws Exception {
        return ResultInfoUtil.successResult(mailConfigurationService.updateSendMode(dcMailConfReqVOs.get(0)));
    }

    @GetMapping(value = "query/conf")
    @ApiOperation("Message configuration echo")
    public ResultInfo<List<DcMailConf>> queryConf() throws Exception {
        return ResultInfoUtil.successResult(mailConfigurationService.queryConf());
    }

}
