package com.spartan.dc.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.spartan.dc.core.dto.portal.UserJoinReqVO;
import com.spartan.dc.core.dto.portal.UserJoinUrlTempVO;
import com.spartan.dc.core.enums.*;
import com.spartan.dc.core.exception.GlobalException;
import com.spartan.dc.core.util.common.HttpUtils;
import com.spartan.dc.dao.write.DcChainAccessMapper;
import com.spartan.dc.dao.write.DcChainMapper;
import com.spartan.dc.dao.write.DcSystemConfMapper;
import com.spartan.dc.dao.write.SysDataCenterMapper;
import com.spartan.dc.model.DcChain;
import com.spartan.dc.model.DcChainAccess;
import com.spartan.dc.model.DcSystemConf;
import com.spartan.dc.model.vo.req.AddChainAccessReqVO;
import com.spartan.dc.service.*;
import okhttp3.Interceptor;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.nio.charset.Charset;
import java.util.*;

/**
 * @ClassName PortalParametersServicelmpl
 * @Author wjx
 * @Date 2022/11/3 17:36
 * @Version 1.0
 */

@Service
public class ChainAccessServicelmpl implements ChainAccessService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DcChainService dcChainService;

    @Autowired
    private DcChainAccessService dcChainAccessService;

    @Resource
    private DcChainMapper dcChainMapper;

    @Resource
    private DcSystemConfMapper dcSystemConfMapper;

    @Value("${kongGateWayConfig.userDefaultReqConfig}")
    private String userDefaultReqConfig;

    @Value("${kongGateWayConfig.username}")
    private String userName;

    @Value("${kongGateWayConfig.password}")
    private String password;

    @Override
    public boolean addChainAccess(AddChainAccessReqVO addChainAccessReqVO) {
        // Obtain TPS configuration information
        // Gets the data center configuration information table
        DcSystemConf tpsConf = dcSystemConfMapper.querySystemConfByCode(SystemConfCodeEnum.TPS.getName());
        DcSystemConf tpdConf = dcSystemConfMapper.querySystemConfByCode(SystemConfCodeEnum.TPD.getName());

        if(Objects.isNull(tpsConf)){
            DcSystemConf dcSystemConf = DcSystemConf.builder()
                    .type(DcSystemConfTypeEnum.CHAIN_INFORMATION_ACCESS.getCode())
                    .confCode(SystemConfCodeEnum.TPS.getCode())
                    .confValue(addChainAccessReqVO.getTps().toString())
                    .updateTime(new Date())
                    .build();
            dcSystemConfMapper.insertSelective(dcSystemConf);
        }else{
            tpsConf.setConfValue(addChainAccessReqVO.getTps().toString());
            dcSystemConfMapper.updateByPrimaryKeySelective(tpsConf);
        }

        if(Objects.isNull(tpdConf)){
            DcSystemConf dcSystemConf = DcSystemConf.builder()
                    .type(DcSystemConfTypeEnum.CHAIN_INFORMATION_ACCESS.getCode())
                    .confCode(SystemConfCodeEnum.TPD.getCode())
                    .confValue(addChainAccessReqVO.getTpd().toString())
                    .updateTime(new Date())
                    .build();
            dcSystemConfMapper.insertSelective(dcSystemConf);
        }else{
            tpdConf.setConfValue(addChainAccessReqVO.getTpd().toString());
            dcSystemConfMapper.updateByPrimaryKeySelective(tpdConf);
        }

        addChainAccessReqVO.getNodeConfigs().forEach(model -> {
            DcChain dcChain = dcChainService.selectByPrimaryKey(model.getChainId());
            if (dcChain == null) {
                return;
            }
            dcChain.setGatewayUrl(addChainAccessReqVO.getGatewayUrl());
            dcChain.setWsGatewayUrl(addChainAccessReqVO.getWsGatewayUrl());
            dcChain.setGrpcGatewayUrl(addChainAccessReqVO.getGrpcGatewayUrl());
            dcChain.setChainCode(model.getChainCode());
            dcChain.setWebsocket(model.getWebSocket());
            dcChain.setGatewayType(model.getGatewayType());
            dcChain.setGrpc(model.getGrpc());
            dcChain.setJsonRpc(model.getJsonRpc());
            dcChainService.updateByPrimaryKeySelective(dcChain);
        });

        // notify the gatewayUrl
        Map<String, String> gatewayList = dcChainMapper.getGatewayUrl();
        String gateway = gatewayList.get("gatewayUrl");
        if(!Objects.isNull(gateway)){
            String gatewayUrl = gateway + userDefaultReqConfig;
            Map<String,Object> notifyInfo = new HashMap<>();
            notifyInfo.put("tps", addChainAccessReqVO.getTps());
            notifyInfo.put("tpd", addChainAccessReqVO.getTpd());

            String authHeader = getHeader(userName,password);
            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", authHeader);

            logger.info("Start notifying gateway users of access key information");
            logger.info("Gateway address："+gatewayUrl);
            logger.info("Notify the gateway information："+notifyInfo);
            logger.info("Notify the gateway Header information："+headers);

            try {
                Connection.Response result = HttpUtils.post(gatewayUrl, headers,JSONArray.toJSONString(notifyInfo));
                JSONObject jsonObject = JSONObject.parseObject(result.body());
                if (jsonObject.get("code").toString().equals("0")) {
                    logger.info("Configure user default request restriction notifications：result【{}】", result);
                } else {
                    logger.info("Configuring the user default request restriction notification gateway failed：【{" + result + "}】");
                }
            } catch (GlobalException e) {
                logger.info("Configure user default request restrictions to notify gateway failure exceptions【{}】", e.getMsg());
            } finally {
                return true;
            }

        }
        return true;

    }


    /**
     * Basic Auth Authentication header information
     *
     * @return
     */
    private static String getHeader(String name, String key) {
        String auth = name + ":" + key;
        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("US-ASCII")));
        String authHeader = "Basic " + new String(encodedAuth);
        return authHeader;
    }




}
