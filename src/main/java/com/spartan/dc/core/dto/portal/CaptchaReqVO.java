package com.spartan.dc.core.dto.portal;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * User object sys_user
 *
 * @author xingjie
 */
@Data
public class CaptchaReqVO extends EmailReqVO {

    @ApiModelProperty(value = "captchaType    user_join_captcha_:User access verification code   gas_recharge_captcha_:gas_recharge verification code", required = true, example = "captcha_")
    @NotEmpty(message = "MSG_5000")
    private String captchaType;
}
