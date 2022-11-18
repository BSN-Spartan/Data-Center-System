package com.spartan.dc.dao.write;

import com.spartan.dc.model.ChainSalePrice;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ChainSalePriceMapper {
    int deleteByPrimaryKey(Long salePriceId);

    int insert(ChainSalePrice record);

    int insertSelective(ChainSalePrice record);

    ChainSalePrice selectByPrimaryKey(Long salePriceId);

    int updateByPrimaryKeySelective(ChainSalePrice record);

    int updateByPrimaryKey(ChainSalePrice record);

    ChainSalePrice selectCurrentSalePrice(Long chainId);

    List<Map<String, Object>> queryPriceList(Map<String, Object> condition);

    ChainSalePrice selectSalePriceByChainId(@Param("chainId") Long chainId, @Param("state") Short state);

    Map<String, Object> getSalePriceById(Integer salePriceId);

}