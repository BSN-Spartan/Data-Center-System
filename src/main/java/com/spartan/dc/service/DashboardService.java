package com.spartan.dc.service;

import com.spartan.dc.core.datatables.DataTable;
import com.spartan.dc.core.datatables.PageInfo;
import com.spartan.dc.model.DcGasRechargeRecord;
import com.spartan.dc.model.DcNode;
import com.spartan.dc.model.vo.req.GetBlockNumberReqVO;
import com.spartan.dc.model.vo.resp.NttBalanceRespVO;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Map;

/**
 * @program: spartan_dc
 * @description:
 * @author: kuan
 * @create: 2022-08-20 16:17
 **/
public interface DashboardService {

    NttBalanceRespVO getNttBalance(String address) ;

    String getGasBalance(String address);

    String getBlockNumber(GetBlockNumberReqVO reqVO);

    Map<String, Object> listNodeInfo(PageInfo pageInfo);

    Map<String, Object> listGasRecharge();

    String getNttAddress();
}
