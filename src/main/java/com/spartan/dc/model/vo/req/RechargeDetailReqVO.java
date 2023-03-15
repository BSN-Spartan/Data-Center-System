package com.spartan.dc.model.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.aspectj.bridge.IMessage;

import javax.validation.constraints.NotNull;

/**
 * @author linzijun
 * @version V1.0
 * @date 2023/2/14 15:19
 */
@Data
public class RechargeDetailReqVO {

    @NotNull(message = "RechargeRecord Id can not be empty")
    private Long rechargeRecordId;

}
