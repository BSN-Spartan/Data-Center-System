package com.spartan.dc.model.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author linzijun
 * @version V1.0
 * @date 2023/2/15 11:12
 */
@Data
public class AddTermsServiceReqVO {

    @NotBlank(message = "Terms Content cannot be empty")
    private String termsContent;

}
