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
    @Pattern(regexp = "(^$|^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z\\W]{6,10}$)", message = "The password must contain 6 to 10 letters and numbers")
    private String keystorePassword;

}
