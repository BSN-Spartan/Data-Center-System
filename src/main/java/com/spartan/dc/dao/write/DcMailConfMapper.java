package com.spartan.dc.dao.write;

import com.spartan.dc.model.DcMailConf;

import java.util.List;

public interface DcMailConfMapper {
    int deleteByPrimaryKey(Long maillConfId);

    int insert(DcMailConf record);

    int insertSelective(DcMailConf record);

    DcMailConf selectByPrimaryKey(Long maillConfId);

    int updateByPrimaryKeySelective(DcMailConf record);

    int updateByPrimaryKey(DcMailConf record);

    DcMailConf queryDcMailConf(Short type);


    List<DcMailConf> selectMailConf();

    DcMailConf queryMailConfType(Short type);
}