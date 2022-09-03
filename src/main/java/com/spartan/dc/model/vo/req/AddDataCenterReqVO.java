package com.spartan.dc.model.vo.req;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

/**
 * @author wxq
 * @create 2022/8/18 20:54
 * @description add datacenter
 */
@Data
public class AddDataCenterReqVO {
    @NotEmpty(message = "email can not be empty")
    @Email(message = "email is illegal")
    private String email;

    @NotEmpty(message = "dcCode can not be empty")
    @Size(max = 200, message = "dcCode (maximum 200 characters)")
    private String dcCode;

    @NotEmpty(message = "token can not be empty")
    @Size(max = 500, message = "token (maximum 500 characters)")
    private String token;

    @NotEmpty(message = "nttAccountAddress can not be empty")
    @Size(max = 60, message = "nttAccountAddress (maximum 60 characters)")
    private String nttAccountAddress;
}
