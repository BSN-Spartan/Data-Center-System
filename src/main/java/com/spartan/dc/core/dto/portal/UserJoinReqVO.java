package com.spartan.dc.core.dto.portal;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/** 
 * @author liqiuyue
 * @version V1.0
 * @date 2022 11-04 16:45.
 */
@Data
public class UserJoinReqVO {

	/**
	 * chain ID collection
	 */
	@ApiModelProperty(value = "chain ID collection", name = "chain ID collection" ,example="[1,2,3]")
	@NotEmpty(message = "chainList cannot be empty")
	private List<Long> chainList;

	/**
	 * Mailbox
	 */
	@ApiModelProperty(value = "Mailbox", example = "123@qq.com", required = true)
	@NotBlank(message = "Mailbox cannot be empty")
	@Email(message = "Mailbox format is not correct")
	private String email;

	/**
	 * Verification code
	 */
	@ApiModelProperty(value = "Verification code", example = "123@qq.com", required = true)
	@NotBlank(message = "Verification code cannot be empty")
	private String captchaCode;

}
