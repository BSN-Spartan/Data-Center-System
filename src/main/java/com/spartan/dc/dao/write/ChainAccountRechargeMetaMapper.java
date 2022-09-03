package com.spartan.dc.dao.write;

import com.spartan.dc.model.ChainAccountRechargeMeta;

import java.math.BigInteger;

public interface ChainAccountRechargeMetaMapper {
    int deleteByPrimaryKey(Long rechargeMetaId);

    int insert(ChainAccountRechargeMeta record);

    int insertSelective(ChainAccountRechargeMeta record);

    ChainAccountRechargeMeta selectByPrimaryKey(Long rechargeMetaId);

    int updateByPrimaryKeySelective(ChainAccountRechargeMeta record);

    int updateByPrimaryKey(ChainAccountRechargeMeta record);

    int insertRechargeMetaRecord(ChainAccountRechargeMeta record);
}