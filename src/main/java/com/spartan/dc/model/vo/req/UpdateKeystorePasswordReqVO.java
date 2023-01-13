package com.spartan.dc.model.vo.req;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

/**
 * @author wxq
 * @create 2022/11/16 16:40
 * @description keystore password
 */
@Data
public class UpdateKeystorePasswordReqVO {
    @NotEmpty(message = "password can not be empty")
    private String keystorePassword;

}
