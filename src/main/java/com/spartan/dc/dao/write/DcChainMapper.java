package com.spartan.dc.dao.write;

import com.spartan.dc.model.DcChain;

import java.util.List;
import java.util.Map;

public interface DcChainMapper {
    int deleteByPrimaryKey(Long chainId);

    int insert(DcChain record);

    int insertSelective(DcChain record);

    DcChain selectByPrimaryKey(Long chainId);

    int updateByPrimaryKeySelective(DcChain record);

    int updateByPrimaryKey(DcChain record);

    List<Map<String, Object>> queryChainList(Map<String, Object> condition);

    List<DcChain> getOpbChainList();

    List<DcChain> getAll();
}