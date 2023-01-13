package com.spartan.dc.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.spartan.dc.core.datatables.DataTable;
import com.spartan.dc.core.dto.ResultInfo;
import com.spartan.dc.core.dto.ResultInfoUtil;
import com.spartan.dc.core.enums.DcChainAccessStateEnum;
import com.spartan.dc.core.enums.SystemConfCodeEnum;
import com.spartan.dc.core.exception.GlobalException;
import com.spartan.dc.core.util.common.HttpUtils;
import com.spartan.dc.core.vo.req.DcChainAccessReqVO;
import com.spartan.dc.dao.write.DcChainAccessMapper;
import com.spartan.dc.dao.write.DcChainMapper;
import com.spartan.dc.dao.write.DcSystemConfMapper;
import com.spartan.dc.model.DcChainAccess;
import com.spartan.dc.model.DcSystemConf;
import com.spartan.dc.service.DcChainAccessService;
import com.spartan.dc.service.SendMessageService;
import org.apache.commons.codec.binary.Base64;
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
 * @author linzijun
 * @version V1.0
 * @date 2022/11/7 18:08
 */
@Service
public class DcChainAccessServiceImpl implements DcChainAccessService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private DcChainAccessMapper dcChainAccessMapper;

    @Resource
    private DcSystemConfMapper dcSystemConfMapper;

    @Resource
    private DcChainMapper dcChainMapper;

    @Value("${kongGateWayConfig.userAccessKeyUrl}")
    private String userAccessKeyUrl;

    @Value("${kongGateWayConfig.username}")
    private String userName;

    @Value("${kongGateWayConfig.password}")
    private String password;

    @Override
    public DcChainAccess selectByPrimaryKey(Long chainAccessId) {
        return dcChainAccessMapper.selectByPrimaryKey(chainAccessId);
    }

    @Override
    public int updateByPrimaryKeySelective(DcChainAccess record) {
        return dcChainAccessMapper.updateByPrimaryKeySelective(record);
    }

    /**
     * Disable currently active
     */
    @Override
    public void disableTheCurrentEnabled() {
        DcChainAccess dcChainAccess = this.getCurrentEnabled();
        if (dcChainAccess != null) {
            dcChainAccess.setState(DcChainAccessStateEnum.BLOCK_UP.getCode());
            dcChainAccess.setUpdateTime(new Date());
            dcChainAccessMapper.updateByPrimaryKeySelective(dcChainAccess);
        }
    }

    /**
     * Get the currently enabled
     *
     * @return
     */
    @Override
    public DcChainAccess getCurrentEnabled() {
        return dcChainAccessMapper.getCurrentEnabled();
    }

    @Override
    public int insertSelective(DcChainAccess record) {
        return dcChainAccessMapper.insertSelective(record);
    }

    @Override
    public Map<String, Object> queryList(DataTable<Map<String, Object>> dataTable) {
        PageHelper.startPage(dataTable.getParam().getPageIndex(), dataTable.getParam().getPageSize());
        List<Map<String, Object>> list = dcChainAccessMapper.queryList(dataTable.getCondition());
        // TPS、TPD
        if (list != null && list.size() > 0) {
            DcSystemConf tpsConf = dcSystemConfMapper.querySystemConfByCode(SystemConfCodeEnum.TPS.getCode());
            DcSystemConf tpdConf = dcSystemConfMapper.querySystemConfByCode(SystemConfCodeEnum.TPD.getName());
            for (Map<String, Object> map : list) {
                map.put("defaultTps", tpsConf.getConfValue());
                map.put("defaultTpd", tpdConf.getConfValue());
            }
        }
        return dataTable.getReturnData(list);
    }

    @Override
    public ResultInfo updateAccessInformation(DcChainAccessReqVO dcChainAccessReqVO) {
        // notify the gatewayUrl
        Map<String, String> gatewayList = dcChainMapper.getGatewayUrl();
        String gateway = gatewayList.get("gatewayUrl");
        if (!Objects.isNull(gateway)) {
            String gatewayUrl = gateway + userAccessKeyUrl;
            List<Map<String, Object>> paramList = new ArrayList<>();
            Map<String, Object> accessNotifyInfo = new HashMap<>();

            DcChainAccess dcChains = dcChainAccessMapper.selectByPrimaryKey(dcChainAccessReqVO.getChainAccessId());

            accessNotifyInfo.put("accessKey", dcChainAccessReqVO.getAccessKey()!=null ? dcChainAccessReqVO.getAccessKey() : dcChains.getAccessKey());
            accessNotifyInfo.put("tps", dcChainAccessReqVO.getTps() !=null ? dcChainAccessReqVO.getTps() : dcChains.getTps());
            accessNotifyInfo.put("tpd", dcChainAccessReqVO.getTpd() !=null ? dcChainAccessReqVO.getTpd() : dcChains.getTpd());
            accessNotifyInfo.put("status", dcChainAccessReqVO.getState()!=null ? dcChainAccessReqVO.getState() : dcChains.getState());

            paramList.add(accessNotifyInfo);

            String authHeader = getHeader(userName, password);
            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", authHeader);

            logger.info("Start notifying the gateway");
            logger.info("Gateway address：" + gatewayUrl);
            logger.info("Notify the gateway information：" + paramList);
            logger.info("Notify the gateway Header information：" + headers);

            try {
                Connection.Response result = HttpUtils.post(gatewayUrl, headers, JSONArray.toJSONString(paramList));
                JSONObject jsonObject = JSONObject.parseObject(result.body());
                if (jsonObject.get("code").toString().equals("0")) {
                    logger.info("Start notifying the gateway result notification: Result【{}】", result);
                    DcChainAccess dcChainAccess = DcChainAccess.builder()
                            .chainAccessId(dcChainAccessReqVO.getChainAccessId())
                            .tpd(dcChainAccessReqVO.getTpd())
                            .tps(dcChainAccessReqVO.getTps())
                            .state(dcChainAccessReqVO.getState())
                            .build();
                    dcChainAccessMapper.updateByPrimaryKeySelective(dcChainAccess);
                } else {
                    logger.info("Notifying the gateway failure：【{" + result + "}】");
                    return ResultInfoUtil.errorResult("Notifying the gateway failure");
                }
            } catch (Exception e) {
                logger.info("Notifying the gateway failure exception【{}】", e.getMessage());
                throw new GlobalException("Notifying the gateway failure");
            }

        } else {
            return ResultInfoUtil.errorResult("Gateway address is empty");
        }
        return ResultInfoUtil.successResult("Success");
    }

    private static String getHeader(String name, String key) {
        String auth = name + ":" + key;
        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("US-ASCII")));
        String authHeader = "Basic " + new String(encodedAuth);
        return authHeader;
    }
}
