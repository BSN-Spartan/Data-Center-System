package com.spartan.dc.model.vo.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * @author wxq
 * @create 2022/8/21 9:32
 * @description add dc node
 */
@Data
public class AddDcNodeReqVO {

    private long chainId;

    @NotBlank(message = "nodeAddress can not be empty")
    @Size(max = 500, message = "nodeAddress (maximum 500 characters)")
    private String nodeAddress;

    @NotBlank(message = "applySign can not be empty")
    @Size(max = 500, message = "applySign (maximum 500 characters)")
    private String applySign;

    @NotBlank(message = "rpcAddress can not be empty")
    @Size(max = 500, message = "rpcAddress (maximum 500 characters)")
    private String rpcAddress;

    @NotBlank(message = "nodeCode can not be empty")
    @Size(max = 200, message = "nodeCode (maximum 200 characters)")
    private String nodeCode;

    @Size(max = 500, message = "remarks (maximum 200 characters)")
    private String remarks;

    private String password;
}
