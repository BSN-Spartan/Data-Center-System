package com.spartan.dc.service;

import com.spartan.dc.core.datatables.DataTable;
import com.spartan.dc.core.vo.resp.ChainAccessRespVO;
import com.spartan.dc.core.vo.resp.DcChainRespVO;
import com.spartan.dc.model.DcChain;

import java.util.List;
import java.util.Map;

/**
 * Desc：
 *
 * @Created by 2022-07-16 20:29
 */
public interface DcChainService {

    Map<String, Object> queryChainList(DataTable<Map<String, Object>> dataTable);

    DcChain selectByPrimaryKey(Long chainId);

    List<DcChain> getOpbChainList();

    List<DcChainRespVO> queryChain();

    int updateByPrimaryKeySelective(DcChain record);

    void resetNodeConfig();

    Map<String,String> getGatewayUrl();

    List<ChainAccessRespVO.NodeConfig> getNodeConfigs();

    void queryChainPrice() throws Exception;
}
