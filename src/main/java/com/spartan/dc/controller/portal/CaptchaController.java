package com.spartan.dc.controller.portal;

import com.alibaba.fastjson.JSONObject;
import com.spartan.dc.core.dto.ResultInfo;
import com.spartan.dc.core.dto.ResultInfoUtil;
import com.spartan.dc.core.dto.portal.CaptchaReqVO;
import com.spartan.dc.core.dto.portal.EmailReqVO;
import com.spartan.dc.core.exception.GlobalException;
import com.spartan.dc.core.enums.ApiRespCodeEnum;
import com.spartan.dc.core.enums.MsgCodeEnum;
import com.spartan.dc.core.util.message.ConstantsUtil;
import com.spartan.dc.dao.write.SysDataCenterMapper;
import com.spartan.dc.model.SysDataCenter;
import com.spartan.dc.service.CommonService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Captcha
 *
 * @author sytang
 * @date 2022-05-20
 */
@Slf4j
@RestController
@RequestMapping("/v1/dc/captcha/")
@Api(tags = "Captcha")
public class CaptchaController {

    @Autowired
    private CommonService commonService;

    @Resource
    private SysDataCenterMapper sysDataCenterMapper;

    @Value("${captchaTimeOut}")
    private String captchaTimeOut;


    @PostMapping("get")
    @ApiOperation(value = "Get verification code - user access")
    public ResultInfo getCaptcha(HttpServletRequest req, @Validated @RequestBody EmailReqVO captchaVo) {
        // Verify the request ID
        log.info("Get the verification code of mailbox>>>>>>{}", JSONObject.toJSONString(captchaVo));

        sendMessage(req,captchaVo.getEmail(), ConstantsUtil.USER_JOIN_CAPTCHA_, MsgCodeEnum.USER_JOIN_CAPTCHA_.getCode());


        return ResultInfoUtil.successResult();
    }


    @PostMapping("send")
    @ApiOperation(value = "Get verification code according to business type")
    public ResultInfo sendCaptcha(HttpServletRequest req,@Validated @RequestBody CaptchaReqVO captchaVo) {
        // Verify the request ID
        log.info("Get the verification code of mailbox>>>>>>{}", JSONObject.toJSONString(captchaVo));

        String captchaType = captchaVo.getCaptchaType().toLowerCase();

        switch (captchaType) {
            case ConstantsUtil.USER_JOIN_CAPTCHA_:

                sendMessage(req,captchaVo.getEmail(), ConstantsUtil.USER_JOIN_CAPTCHA_, MsgCodeEnum.USER_JOIN_CAPTCHA_.getCode());

                break;
            case ConstantsUtil.GAS_RECHARGE_CAPTCHA_:

                sendMessage(req,captchaVo.getEmail(), ConstantsUtil.GAS_RECHARGE_CAPTCHA_, MsgCodeEnum.GAS_RECHARGE_CAPTCHA_.getCode());

                break;
            default:
                return ResultInfoUtil.failure(ApiRespCodeEnum.REQ_ERROR.getCode());
        }

        return ResultInfoUtil.success();
    }


    private void sendSaveDcCaptcha(HttpServletRequest req,String email) {
        // Check if the mailbox exists
        SysDataCenter sysDataCenter = sysDataCenterMapper.getDcByEmail(email);
        if (Objects.isNull(sysDataCenter)) {
            throw new GlobalException(ApiRespCodeEnum.DATA_CENTER_NO_EXISTS.getCode());
        }
        sendMessage(req,email, ConstantsUtil.USER_JOIN_CAPTCHA_, MsgCodeEnum.USER_JOIN_CAPTCHA_.getCode());
    }


    private void sendMessage(HttpServletRequest req,String email, String emailType, String emailMsgCode) {
        String redisKey = emailType + email;
        String redisCode = "";
        log.info("Get the verification code of mailbox>>>>>>{}------{}", email, redisCode);
        if (redisCode != null && redisCode.length() > 0) {
            throw new GlobalException(ApiRespCodeEnum.REQ_CODE_REPEAT.getCode());
        }
        // Generate verification code
        Integer code = (int) ((Math.random() * 9 + 1) * 100000);

        log.info("Get the verification code of mailbox>>>>>>{}>>>>>>{}", redisKey, code);
        HttpSession session = req.getSession();
        session.setAttribute(emailType+email, code.toString());
        System.out.println(session.getAttribute(emailType+email));

        Map<String, Object> replaceContentMap = new HashMap<>();
        replaceContentMap.put("captcha_", code.toString());
        replaceContentMap.put("minutes_", captchaTimeOut);
        commonService.sendEmail(emailMsgCode, replaceContentMap, replaceContentMap, null, email);
    }

}
