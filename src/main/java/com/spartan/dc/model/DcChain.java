package com.spartan.dc.model;

public class DcChain {
    private Long chainId;

    private String chainType;

    private String chainName;

    private String rechargeUnit;

    private String chainCode;

    private Short gatewayType;

    private String gatewayUrl;

    private String wsGatewayUrl;

    private String grpcGatewayUrl;

    private Short jsonRpc;

    private Short websocket;

    private Short grpc;

    public Long getChainId() {
        return chainId;
    }

    public void setChainId(Long chainId) {
        this.chainId = chainId;
    }

    public String getChainType() {
        return chainType;
    }

    public void setChainType(String chainType) {
        this.chainType = chainType == null ? null : chainType.trim();
    }

    public String getChainName() {
        return chainName;
    }

    public void setChainName(String chainName) {
        this.chainName = chainName == null ? null : chainName.trim();
    }

    public String getRechargeUnit() {
        return rechargeUnit;
    }

    public void setRechargeUnit(String rechargeUnit) {
        this.rechargeUnit = rechargeUnit == null ? null : rechargeUnit.trim();
    }

    public String getChainCode() {
        return chainCode;
    }

    public void setChainCode(String chainCode) {
        this.chainCode = chainCode == null ? null : chainCode.trim();
    }

    public Short getGatewayType() {
        return gatewayType;
    }

    public void setGatewayType(Short gatewayType) {
        this.gatewayType = gatewayType;
    }

    public String getGatewayUrl() {
        return gatewayUrl;
    }

    public void setGatewayUrl(String gatewayUrl) {
        this.gatewayUrl = gatewayUrl == null ? null : gatewayUrl.trim();
    }

    public String getWsGatewayUrl() {
        return wsGatewayUrl;
    }

    public void setWsGatewayUrl(String wsGatewayUrl) {
        this.wsGatewayUrl = wsGatewayUrl == null ? null : wsGatewayUrl.trim();
    }

    public String getGrpcGatewayUrl() {
        return grpcGatewayUrl;
    }

    public void setGrpcGatewayUrl(String grpcGatewayUrl) {
        this.grpcGatewayUrl = grpcGatewayUrl == null ? null : grpcGatewayUrl.trim();
    }

    public Short getJsonRpc() {
        return jsonRpc;
    }

    public void setJsonRpc(Short jsonRpc) {
        this.jsonRpc = jsonRpc;
    }

    public Short getWebsocket() {
        return websocket;
    }

    public void setWebsocket(Short websocket) {
        this.websocket = websocket;
    }

    public Short getGrpc() {
        return grpc;
    }

    public void setGrpc(Short grpc) {
        this.grpc = grpc;
    }
}