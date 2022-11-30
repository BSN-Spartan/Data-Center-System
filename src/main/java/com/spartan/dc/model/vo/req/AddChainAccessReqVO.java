package com.spartan.dc.model.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.List;

/**
 * @author linzijun
 * @version V1.0
 * @date 2022/11/7 19:10
 */
@Data
public class AddChainAccessReqVO implements Serializable {

    @ApiModelProperty(value = "tps", required = false)
    private String tps;

    @ApiModelProperty(value = "tpd", required = false)
    private String tpd;

    @Length(max = 200, message = "Gateway address length exceeds the limit")
    @NotBlank(message = "Gateway address cannot be empty")
    @ApiModelProperty(value = "gateway url", required = true)
    private String gatewayUrl;

    @Length(max = 200, message = "Gateway address length exceeds the limit")
    @ApiModelProperty(value = "websocket gateway url", required = true)
    private String wsGatewayUrl;

    @Length(max = 200, message = "Gateway address length exceeds the limit")
    @ApiModelProperty(value = "grpc gateway url", required = true)
    private String grpcGatewayUrl;

    @Valid
    @NotNull(message = "Node configuration cannot be empty")
    @Size(min = 1, message = "Chain ID cannot be empty")
    @ApiModelProperty(value = "node configs", required = true)
    private List<NodeConfig> nodeConfigs;

    @Data
    public static class NodeConfig {

        @NotNull(message = "The length of the chain ID exceeds the limit")
        @ApiModelProperty(value = "chain id", required = true)
        private Long chainId;

        @Length(max = 20, message = "The length of the chain ID exceeds the limit")
        @NotBlank(message = "Chain ID cannot be empty")
        @ApiModelProperty(value = "Chain ID, length limit 20", required = true)
        private String chainCode;

        @Max(value = 1, message = "jsonRpc type exception")
        @Min(value = 0, message = "jsonRpc type exception")
        @NotNull(message = "jsonRpc cannot be empty")
        @ApiModelProperty(value = "Json RPC  0：nonsupport 1：support", required = true)
        private Short jsonRpc;

        @Max(value = 1, message = "WebSocket type exception")
        @Min(value = 0, message = "WebSocket type exception")
        @NotNull(message = "webSocket cannot be empty")
        @ApiModelProperty(value = "WebSocket 0：nonsupport 1：support", required = true)
        private Short webSocket;

        @Max(value = 1, message = "gRPC type exception")
        @Min(value = 0, message = "gRPC type exception")
        @NotNull(message = "grpc cannot be empty")
        @ApiModelProperty(value = "gRPC 0：nonsupport 1：support", required = true)
        private Short grpc;

        @Max(value = 1, message = "gateway Type type exception")
        @Min(value = 0, message = "gateway Type type exception")
        @NotNull(message = "gateway Type cannot be empty")
        @ApiModelProperty(value = "gatewayType 0：nonsupport 1：support", required = true)
        private Short gatewayType;

    }

}
