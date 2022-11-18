package com.spartan.dc.dao.write;

import com.spartan.dc.core.vo.resp.ChainAccessRespVO;
import com.spartan.dc.core.vo.resp.DcChainRespVO;
import com.spartan.dc.model.DcChain;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface DcChainMapper {

    int deleteByPrimaryKey(Long chainId);

    int insert(DcChain record);

    int insertSelective(DcChain record);

    DcChain selectByPrimaryKey(Long chainId);

    int updateByPrimaryKeySelective(DcChain record);

    int updateByPrimaryKey(DcChain record);

    List<DcChainRespVO> queryChain();

    List<DcChain> getOpbChainList();

    List<DcChain> getAll();

    List<Map<String, Object>> queryChainList(Map<String, Object> condition);

	DcChain getChainByChainId(Long chainId);

	void resetNodeConfig();

    Map<String, String> getGatewayUrl();

    List<ChainAccessRespVO.NodeConfig> getNodeConfigs();
}