package com.spartan.dc.dao.write;

import com.spartan.dc.model.ChainPrice;

public interface ChainPriceMapper {

    int deleteByPrimaryKey(Long chainPriceId);

    int insert(ChainPrice record);

    int insertSelective(ChainPrice record);

    ChainPrice selectByPrimaryKey(Long chainPriceId);

    int updateByPrimaryKeySelective(ChainPrice record);

    int updateByPrimaryKey(ChainPrice record);

    ChainPrice getOneByChainId(Long chainId);
}