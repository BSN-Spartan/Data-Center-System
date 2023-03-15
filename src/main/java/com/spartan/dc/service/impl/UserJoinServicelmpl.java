package com.spartan.dc.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.spartan.dc.core.dto.portal.SendMessageReqVO;
import com.spartan.dc.core.dto.portal.UserJoinReqVO;
import com.spartan.dc.core.dto.portal.UserJoinUrlTempVO;
import com.spartan.dc.core.enums.*;
import com.spartan.dc.core.exception.GlobalException;
import com.spartan.dc.core.util.common.HttpUtils;
import com.spartan.dc.dao.write.DcChainAccessMapper;
import com.spartan.dc.dao.write.DcChainMapper;
import com.spartan.dc.dao.write.DcSystemConfMapper;
import com.spartan.dc.model.DcChain;
import com.spartan.dc.model.DcChainAccess;
import com.spartan.dc.service.CommonService;
import com.spartan.dc.service.SendMessageService;
import com.spartan.dc.service.UserJoinService;
import org.jsoup.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import org.apache.commons.codec.binary.Base64;

import java.nio.charset.Charset;
import java.util.*;

/**
 * @ClassName PortalParametersServicelmpl
 * @Author wjx
 * @Date 2022/11/3 17:36
 * @Version 1.0
 */

@Service
public class UserJoinServicelmpl implements UserJoinService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private DcSystemConfMapper dcSystemConfMapper;

    @Resource
    private DcChainAccessMapper dcChainAccessMapper;

    @Resource
    private DcChainMapper dcChainMapper;

    @Autowired
    private CommonService commonService;

    @Autowired
    private SendMessageService sendMessageService;

    @Value("${kongGateWayConfig.userAccessKeyUrl}")
    private String userAccessKeyUrl;

    @Value("${kongGateWayConfig.username}")
    private String userName;

    @Value("${kongGateWayConfig.password}")
    private String password;

    @Override
    public boolean userJoinChain(UserJoinReqVO userJoinReqVO) {

        // Queries whether the user is connected to the chain
        String accessKey = "";
        DcChainAccess dcChainAccess = dcChainAccessMapper.selectByEmail(userJoinReqVO.getEmail());

        DcChainAccess dcChainAccessState = dcChainAccessMapper.queryChainAccessState(userJoinReqVO.getEmail());
        if (Objects.nonNull(dcChainAccessState) && dcChainAccessState.getState().equals(DcChainAccessStateEnum.BLOCK_UP.getCode())) {
            throw new GlobalException("The status of access is disabled, please do not apply again.");
        }

        if (Objects.isNull(dcChainAccess)) {

            // Generation of access key UUID
            accessKey = UUID.randomUUID().toString().replaceAll("-", "");

            // Gets the data center configuration information table
//            String tpdValue = dcSystemConfMapper.querySystemValue(DcSystemConfTypeEnum.CHAIN_INFORMATION_ACCESS.getCode(), SystemConfCodeEnum.TPD.getName());
//            String tpsValue = dcSystemConfMapper.querySystemValue(DcSystemConfTypeEnum.CHAIN_INFORMATION_ACCESS.getCode(), SystemConfCodeEnum.TPS.getName());
//            if (StringUtils.isBlank(tpdValue) || StringUtils.isBlank(tpsValue)) {
//                throw new GlobalException("Chain access information cannot be empty");
//            }

            // Save user access information
            dcChainAccess = DcChainAccess.builder()
                    .accessKey(accessKey)
                    .contactsEmail(userJoinReqVO.getEmail())
                    .state(DcChainAccessStateEnum.START_USING.getCode())
                    .notifyState(DcChainAccessNotifyStateEnum.NOTIFY_FAILURE.getCode())
                    .tps(-2)
                    .tpd(-2)
                    .createTime(new Date())
                    .updateTime(new Date())
                    .build();

            dcChainAccessMapper.insertSelective(dcChainAccess);
        } else {
            accessKey = dcChainAccess.getAccessKey();
        }
        if (dcChainAccess.getNotifyState().equals(DcChainAccessNotifyStateEnum.NOTIFY_FAILURE.getCode())) {
            // notify the gatewayUrl
            Map<String, String> gatewayList = dcChainMapper.getGatewayUrl();
            String gateway = gatewayList.get("gatewayUrl");
            if (!Objects.isNull(gateway)) {
                String gatewayUrl = gateway + userAccessKeyUrl;
                List<Map<String, Object>> paramList = new ArrayList<>();
                Map<String, Object> accessNotifyInfo = new HashMap<>();
                accessNotifyInfo.put("accessKey", accessKey);
                accessNotifyInfo.put("status", dcChainAccess.getState());
                accessNotifyInfo.put("tps", -2);
                accessNotifyInfo.put("tpd", -2);

                paramList.add(accessNotifyInfo);

                String authHeader = getHeader(userName, password);
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", authHeader);

                logger.info("Start notifying gateway users of access key information");
                logger.info("Gateway address：" + gatewayUrl);
                logger.info("Notify the gateway information：" + paramList);
                logger.info("Notify the gateway Header information：" + headers);

                try {
                    Connection.Response result = HttpUtils.post(gatewayUrl, headers, JSONArray.toJSONString(paramList));
                    JSONObject jsonObject = JSONObject.parseObject(result.body());
                    if (jsonObject.get("code").toString().equals("0")) {
                        logger.info("IPFS pushes the user's configuration to the gateway result notification: Result【{}】", result);
                        dcChainAccess.setNotifyState(DcChainAccessNotifyStateEnum.NOTIFY_SUCCESS.getCode());
                        dcChainAccessMapper.updateByPrimaryKeySelective(dcChainAccess);
                    } else {
                        logger.info("User access key notifies gateway failure：【{" + result + "}】");
                    }
                } catch (Exception e) {
                    logger.info("User access key notifies gateway failure exception【{}】", e.getMessage());
                    throw new GlobalException("User access key notifies gateway failure");
                }

            }
        }

        String dcCenterName = dcSystemConfMapper.querySystemValue(DcSystemConfTypeEnum.PORTAL_INFORMATION.getCode(), SystemConfCodeEnum.HEADLINE.getCode());
        String messageTitle = "BSN Spartan Data Center Notification: Network Access Information";
        if (StringUtils.isNotBlank(dcCenterName)) {
            messageTitle = messageTitle.replace("BSN Spartan Data Center", dcCenterName);
        }

        String messageBody = getEmailBody(userJoinReqVO, accessKey);

        // Send email
        Map<String, Object> replaceTitleMap = new HashMap<>();
        replaceTitleMap.put("user_join_title_", messageTitle);

        Map<String, Object> replaceContentMap = new HashMap<>();
        replaceContentMap.put("user_join_content_", messageBody);

        // Recipient
        List<String> receivers = Lists.newArrayList();
        receivers.add(userJoinReqVO.getEmail());

        SendMessageReqVO sendMessageReqVO = new SendMessageReqVO();
        sendMessageReqVO.setMsgCode(MsgCodeEnum.USER_JOIN_CHAIN.getCode());
        sendMessageReqVO.setReplaceTitleMap(replaceTitleMap);
        sendMessageReqVO.setReplaceContentMap(replaceContentMap);
        sendMessageReqVO.setReceivers(receivers);
        boolean sendEmailResult = sendMessageService.sendMessage(sendMessageReqVO);

        return sendEmailResult;

    }


    /**
     * Basic Auth authentication header information
     *
     * @return
     */
    private static String getHeader(String name, String key) {
        String auth = name + ":" + key;
        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("US-ASCII")));
        String authHeader = "Basic " + new String(encodedAuth);
        return authHeader;
    }

    private String getEmailBody(UserJoinReqVO userJoinReqVO, String accessKey) {
        // Send user access email
        UserJoinUrlTempVO ethReplaceContentMap = new UserJoinUrlTempVO();
        UserJoinUrlTempVO cosmosReplaceContentMap = new UserJoinUrlTempVO();
        UserJoinUrlTempVO polygonReplaceContentMap = new UserJoinUrlTempVO();

        for (Long chainId : userJoinReqVO.getChainList()) {
            // get Chain info
            DcChain dcChain = dcChainMapper.getChainByChainId(chainId);
            String chainCode = dcChain.getChainCode();

            String rpcAccessUrl = "";
            String wsAccessUrl = "";
            String grpcAccessUrl = "";
            String evmRpcAccessUrl = "";
            String evmWsAccessUrl = "";


            // Address of access
            if (dcChain.getJsonRpc().equals(DcChainGatewayType.SUPPORT.getCode()) && dcChain.getGatewayUrl().trim().length() > 0) {
                rpcAccessUrl = dcChain.getGatewayUrl() + "/api/" + accessKey + "/" + chainCode + "/rpc/";
                evmRpcAccessUrl = dcChain.getGatewayUrl() + "/api/" + accessKey + "/" + chainCode + "/evmrpc/";
            }

            if (dcChain.getWebsocket().equals(DcChainGatewayType.SUPPORT.getCode()) && dcChain.getWsGatewayUrl().trim().length() > 0) {
                wsAccessUrl = dcChain.getWsGatewayUrl() + "/api/" + accessKey + "/" + chainCode + "/ws/";
                evmWsAccessUrl = dcChain.getWsGatewayUrl() + "/api/" + accessKey + "/" + chainCode + "/evmws/";
            }

            if (dcChain.getGrpc().equals(DcChainGatewayType.SUPPORT.getCode()) && dcChain.getGrpcGatewayUrl().trim().length() > 0) {
                grpcAccessUrl = dcChain.getGrpcGatewayUrl();
            }


            if (chainId.equals(ChainTypeEnum.ETH.getCode().longValue())) {
                ethReplaceContentMap.setAccessKey(accessKey);
                ethReplaceContentMap.setRpcUrl(rpcAccessUrl);
                ethReplaceContentMap.setWsURl(wsAccessUrl);
            } else if (chainId.equals(ChainTypeEnum.COSMOS.getCode().longValue())) {
                cosmosReplaceContentMap.setAccessKey(accessKey);
                cosmosReplaceContentMap.setRpcUrl(rpcAccessUrl);
                cosmosReplaceContentMap.setEvmRpcUrl(evmRpcAccessUrl);
                cosmosReplaceContentMap.setWsURl(wsAccessUrl);
                cosmosReplaceContentMap.setEvmWsUrl(evmWsAccessUrl);
                cosmosReplaceContentMap.setGrpcUrl(grpcAccessUrl);
                cosmosReplaceContentMap.setChainCode(chainCode);
            } else if (chainId.equals(ChainTypeEnum.POLYGON.getCode().longValue())) {
                polygonReplaceContentMap.setAccessKey(accessKey);
                polygonReplaceContentMap.setRpcUrl(rpcAccessUrl);
                polygonReplaceContentMap.setWsURl(wsAccessUrl);
            }

        }

        // Email Header
        String emailHeader = "Welcome to BSN Spartan Network! <br/>\n" +
                "You can access to BSN Spartan Network via these nodes:<br/><br/>";

//        String dcCenterName = dcSystemConfMapper.querySystemValue(DcSystemConfTypeEnum.PORTAL_INFORMATION.getCode(), SystemConfCodeEnum.TITLE.getCode());
//        if(StringUtils.isNotBlank(dcCenterName)){
//            emailHeader = emailHeader.replace("BSN Spartan Network",dcCenterName);
//        }

        // Splice the content of the email

        String emailContentEth = "";
        String emailContentCosmos = "";
        String emailContentPolygon = "";

        if (ethReplaceContentMap.getAccessKey() != null) {
            emailContentEth = "<br/>Spartan-I(Powered by NC Ethereum)<br/> Access Key:${accessKey_}<br/>".replace("${accessKey_}", accessKey);
            if (ethReplaceContentMap.getRpcUrl().length() > 0) {
                emailContentEth = emailContentEth + "Json RPC Access URL:<br/>${rpcUrl_}<br/>".replace("${rpcUrl_}", ethReplaceContentMap.getRpcUrl());
            }
            if (ethReplaceContentMap.getWsURl().length() > 0) {
                emailContentEth = emailContentEth + "WebSocket Access URL:<br/>${wsUrl_}<br/><br/>".replace("${wsUrl_}", ethReplaceContentMap.getWsURl());
            }
        }

        if (cosmosReplaceContentMap.getAccessKey() != null) {
            emailContentCosmos = "<br/>Spartan-II(Powered by NC Cosmos)<br/> Access Key:${accessKey_}<br/>".replace("${accessKey_}", accessKey);
            if (cosmosReplaceContentMap.getRpcUrl().length() > 0) {
                emailContentCosmos = emailContentCosmos + "Json RPC Access URL(EVM module):<br/>${evmRpcUrl_}<br/> Json RPC Access URL(Native module):<br/>${rpcUrl_}<br/>".replace("${evmRpcUrl_}", cosmosReplaceContentMap.getEvmRpcUrl()).replace("${rpcUrl_}", cosmosReplaceContentMap.getRpcUrl());
            }
            if (cosmosReplaceContentMap.getWsURl().length() > 0) {
                emailContentCosmos = emailContentCosmos + "WebSocket Access URL(EVM module):<br/>${evmWsUrl_}<br/> WebSocket Access URL(Native module):<br/>${wsUrl_}<br/>".replace("${evmWsUrl_}", cosmosReplaceContentMap.getEvmWsUrl()).replace("${wsUrl_}", cosmosReplaceContentMap.getWsURl());
            }
            if (cosmosReplaceContentMap.getGrpcUrl().length() > 0) {
                emailContentCosmos = emailContentCosmos + "GRPC Access URL:${grpcUrl_}<br/>GRPC Header Configuration:<br/>x-api-key: ${accessKey_}<br/>x-api-chain-type: ${chainCode_}<br/><br/>".replace("${grpcUrl_}", cosmosReplaceContentMap.getGrpcUrl()).replace("${accessKey_}", cosmosReplaceContentMap.getAccessKey()).replace("${chainCode_}", cosmosReplaceContentMap.getChainCode());
            }
        }

        if (polygonReplaceContentMap.getAccessKey() != null) {
            emailContentPolygon = "<br/>Spartan-III(Powered by NC PolygonEdge)<br/> Access Key:${accessKey_}<br/>".replace("${accessKey_}", accessKey);
            if (polygonReplaceContentMap.getRpcUrl().length() > 0) {
                emailContentPolygon = emailContentPolygon + "Json RPC Access URL:<br/>${rpcUrl_}<br/>".replace("${rpcUrl_}", polygonReplaceContentMap.getRpcUrl());
            }
            if (polygonReplaceContentMap.getWsURl().length() > 0) {
                emailContentPolygon = emailContentPolygon + "WebSocket Access URL:<br/>${wsUrl_}<br/><br/>".replace("${wsUrl_}", polygonReplaceContentMap.getWsURl());
            }
        }

        String emailContent = "";
        if (emailContentEth.length() > 0 || emailContentPolygon.length() > 0 || emailContentCosmos.length() > 0) {
            emailContent = emailHeader + emailContentEth + emailContentCosmos + emailContentPolygon;
        }

        return emailContent;


    }


    public static void main(String[] args) {
        String gateway = "http://10.0.7.135:18601/gateway/api/v0/accessKey/save";
        List<Map<String, Object>> paramList = new ArrayList<>();
        Map<String, Object> accessNotifyInfo = new HashMap<>();
        accessNotifyInfo.put("accessKey", "11111111111111111");
        accessNotifyInfo.put("status", 1);
        accessNotifyInfo.put("tps", 1);
        accessNotifyInfo.put("tpd", 1);

        paramList.add(accessNotifyInfo);
        String authHeader = getHeader("cuining", "zaq1xsw2");
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", authHeader);
        try {
//            String result = OkHttpUtils.doPost(gateway,null, JSONArray.toJSONString(paramList));
            Connection.Response result = HttpUtils.post(gateway, headers, JSONArray.toJSONString(paramList));
            JSONObject jsonObject = JSONObject.parseObject(result.body());
            System.out.println(jsonObject);
            System.out.println(jsonObject.get("code").toString().equals("0"));
        } catch (Exception e) {

        }


    }


}
