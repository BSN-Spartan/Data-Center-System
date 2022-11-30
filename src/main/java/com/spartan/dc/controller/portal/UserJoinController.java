package com.spartan.dc.controller.portal;


import com.spartan.dc.core.dto.ResultInfo;
import com.spartan.dc.core.dto.ResultInfoUtil;
import com.spartan.dc.core.dto.portal.UserJoinReqVO;
import com.spartan.dc.core.enums.MsgCodeEnum;
import com.spartan.dc.core.util.message.ConstantsUtil;
import com.spartan.dc.core.util.user.UserGlobals;
import com.spartan.dc.service.UserJoinService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.UUID;

/**
 * User Join
 *
 * @author liqiuyue
 * @date 2022-05-20
 */
@Slf4j
@RestController
@RequestMapping("/v1/dc/chain/")
@Api(tags = "User access")
public class UserJoinController {

    private final static Logger logger = LoggerFactory.getLogger(UserJoinController.class);

    @Autowired
    private UserJoinService userJoinService;

    @PostMapping(value = "userJoin")
    @ResponseBody
    public ResultInfo userJoinChain(HttpSession session,@Validated @RequestBody UserJoinReqVO userJoinReqVO) {

        logger.info("User......Join......");
        // check code
        if (StringUtils.isEmpty(userJoinReqVO.getCaptchaCode())) {
            return ResultInfoUtil.errorResult("Verification code cannot be empty");
        }
        Object code = session.getAttribute(ConstantsUtil.USER_JOIN_CAPTCHA_+userJoinReqVO.getEmail());
        if (code == null) {
            return ResultInfoUtil.errorResult("Verification code has expired");
        }
        if (!code.toString().equalsIgnoreCase(userJoinReqVO.getCaptchaCode())) {
            return ResultInfoUtil.errorResult("Verification code is incorrect");
        }

        if (StringUtils.isEmpty(userJoinReqVO.getEmail())) {
            return ResultInfoUtil.errorResult("Email cannot be empty");
        }
        if(userJoinReqVO.getChainList().size()==0){
            return ResultInfoUtil.errorResult("chainList cannot be empty");
        }

        boolean result = userJoinService.userJoinChain(userJoinReqVO);
        if (result) {
            session.removeAttribute(ConstantsUtil.USER_JOIN_CAPTCHA_+userJoinReqVO.getEmail());
            return ResultInfoUtil.successResult("successful");
        } else {
            return ResultInfoUtil.errorResult("failed");
        }

    }

}
