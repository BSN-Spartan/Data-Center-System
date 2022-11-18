package com.spartan.dc.core.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author linzijun
 * @version V1.0
 * @date 2022/11/8 11:20
 */
@Data
public class ChainAccessRespVO implements Serializable {

    @ApiModelProperty(value = "tps", required = false)
    private Integer tps;

    @ApiModelProperty(value = "tpd", required = false)
    private Integer tpd;

    @ApiModelProperty(value = "Gateway address", required = false)
    private String gatewayUrl;


    @ApiModelProperty(value = "Gateway address", required = false)
    private String wsGatewayUrl;


    @ApiModelProperty(value = "Gateway address", required = false)
    private String grpcGatewayUrl;

    @ApiModelProperty(value = "Node configuration", required = false)
    private List<ChainAccessRespVO.NodeConfig> nodeConfigs;

    @Data
    public static class NodeConfig {

        @ApiModelProperty(value = "chain id", required = true)
        private Long chainId;

        @ApiModelProperty(value = "Chain type", required = true)
        private String chainType;

        @ApiModelProperty(value = "Chain name", required = true)
        private String chainName;

        @ApiModelProperty(value = "Whether to support gateway    0:nonsupport    1：support", required = true)
        private Short gatewayType;

        @ApiModelProperty(value = "Chain ID, length limit is 20", required = true)
        private String chainCode;

        @ApiModelProperty(value = "Json RPC  0：nonsupport 1：support", required = true)
        private Short jsonRpc;

        @ApiModelProperty(value = "WebSocket 0：nonsupport 1：support", required = true)
        private Short webSocket;

        @ApiModelProperty(value = "gRPC 0：nonsupport 1：support", required = true)
        private Short grpc;

    }

}
