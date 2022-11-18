package com.spartan.dc.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.spartan.dc.core.datatables.PageInfo;
import com.reddate.spartan.SpartanSdkClient;
import com.reddate.spartan.dto.wuhanchain.ReqJsonRpcBean;
import com.reddate.spartan.dto.wuhanchain.RespJsonRpcBean;
import com.reddate.spartan.util.http.RestTemplateUtil;
import com.spartan.dc.core.datatables.DataTable;
import com.spartan.dc.core.dto.dc.DataCenter;
import com.spartan.dc.core.exception.GlobalException;
import com.spartan.dc.core.enums.ChainTypeEnum;
import com.spartan.dc.dao.write.DcGasRechargeRecordMapper;
import com.spartan.dc.dao.write.DcNodeMapper;
import com.spartan.dc.dao.write.SysDataCenterMapper;
import com.spartan.dc.model.vo.req.GetBlockNumberReqVO;
import com.spartan.dc.model.vo.resp.NttBalanceRespVO;
import com.spartan.dc.service.DashboardService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.web3j.utils.Numeric;
import org.web3j.utils.Strings;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @program: spartan_dc
 * @description:
 * @author: kuan
 * @create: 2022-08-20 16:24
 **/
@Slf4j
@Service
public class DashboardServiceImpl extends BaseService implements DashboardService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Resource
    private SpartanSdkClient spartanSdkClient;

    @Autowired
    private SysDataCenterMapper sysDataCenterMapper;
    @Autowired
    private DcNodeMapper dcNodeMapper;
    @Autowired
    private DcGasRechargeRecordMapper dcGasRechargeRecordMapper;


    @Override
    public NttBalanceRespVO getNttBalance(String address) {
        NttBalanceRespVO resp = new NttBalanceRespVO();

        try {
            BigDecimal nttCount = spartanSdkClient.nTTService.balanceOf(address);
            resp.setNttBalance(nttCount.toString());
            resp.setNttWallet(address);
            return resp;
        } catch (Exception e) {
            logger.error("Failed to get NTT balance:", e);
            throw new GlobalException("Failed to get NTT balance");
        }
    }


    @Override
    public String getNttAddress() {

        DataCenter dataCenter = sysDataCenterMapper.getDataCenter();
        if (dataCenter == null || Strings.isEmpty(dataCenter.getNttAccountAddress())) {
            return "";
        }
        return dataCenter.getNttAccountAddress();
    }

    @Override
    public String getGasBalance(String address) {

        try {
            BigInteger balance= spartanSdkClient.nTTService.balanceOfGas(address);
            return balance.toString();
        } catch (Exception e) {
            logger.error("Failed to get GAS balance:", e);
            throw new GlobalException("Failed to get GAS balance");
        }
    }

    @Override
    public String getBlockNumber(GetBlockNumberReqVO reqVO) {
        try {
            if (ChainTypeEnum.ETH.getCode().equals(reqVO.getChainId()) || ChainTypeEnum.POLYGON.getCode().equals(reqVO.getChainId())) {
                ReqJsonRpcBean reqJsonRpcBean = new ReqJsonRpcBean();
                reqJsonRpcBean.setMethod("eth_blockNumber");
                reqJsonRpcBean.setParams(new ArrayList());
                RespJsonRpcBean respJsonRpcBean = RestTemplateUtil.sendPost(reqVO.getRpcAddr(), reqJsonRpcBean, RespJsonRpcBean.class);
                if (Objects.isNull(respJsonRpcBean)) {
                    return null;
                }
                if (Objects.nonNull(respJsonRpcBean.getError())) {
                    logger.error("Get blockNumber error:chainId-->{},rpcURl-->{}", reqVO.getChainId(), reqVO.getRpcAddr());
                    return null;
                }
                return Numeric.decodeQuantity(respJsonRpcBean.getResult().toString()).toString();
            } else if (ChainTypeEnum.COSMOS.getCode().equals(reqVO.getChainId())) {
                String response = RestTemplateUtil.sendGet(reqVO.getRpcAddr() + "/abci_info");
                if (StringUtils.isNotBlank(response)) {
                    JSONObject jsonObject = JSONObject.parseObject(response);
                    if (Objects.nonNull(jsonObject)) {
                        return jsonObject.getJSONObject("result").getJSONObject("response").getString("last_block_height");
                    }
                }
            }
        } catch (RestClientException e) {
            logger.error("Get blockNumber restError:chainId-->{},rpcURl-->{}", reqVO.getChainId(), reqVO.getRpcAddr(), e);
        }
        return null;
    }

    @Override
    public Map<String, Object> listNodeInfo(PageInfo pageInfo) {
        DataTable<Map<String, Object>> dataTable = new DataTable<>();
        dataTable.setParam(pageInfo);
        PageHelper.startPage(pageInfo.getPageIndex(), pageInfo.getPageSize());

        List<Map<String, Object>> queryNodeList = dcNodeMapper.queryNodeList(dataTable.getCondition());
        return dataTable.getReturnData(queryNodeList);
    }

    @Override
    public Map<String, Object> listGasRecharge() {
        DataTable<Map<String, Object>> dataTable = new DataTable<>();
        PageInfo pageInfo = new PageInfo();
        pageInfo.setPageIndex(1);
        pageInfo.setPageSize(10);
        dataTable.setParam(pageInfo);
        PageHelper.startPage(dataTable.getParam().getPageIndex(), dataTable.getParam().getPageSize());

        List<Map<String, Object>> queryChargeList = dcGasRechargeRecordMapper.queryChargeList(dataTable.getCondition());
        return dataTable.getReturnData(queryChargeList);
    }

}
