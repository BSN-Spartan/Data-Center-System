package com.spartan.dc.model.vo.req;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * @author wxq
 * @create 2022/8/18 19:29
 * @description Generate new wallet
 */
@Data
public class GenerateNewWalletReqVO {
    @NotEmpty(message = "password can not be empty")
    @Pattern(regexp = "(^$|^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z\\W]{6,10}$)", message = "The password must contain 6 to 10 letters and numbers")
    private String password;

    @NotEmpty(message = "privateKey can not be empty")
    @Size(max = 200, message = "privateKey (maximum 200 characters)")
    private String privateKey;
}
