package com.spartan.dc.core.dto.portal;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

/**
 * Desc：
 *
 * @Created by 2022-09-04 19:44
 */
@Data
public class EmailReqVO {

    private static final long serialVersionUID = 1L;

    /**
     * email
     */
    @ApiModelProperty(value = "email", name = "email", required = true, example = "123456@mail.com")
    @NotBlank(message = "Mailbox cannot be empty")
    @Email(message = "Mailbox format is not correct")
    private String email;



}
