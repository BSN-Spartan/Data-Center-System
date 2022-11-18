package com.spartan.dc.dao.write;

import com.spartan.dc.model.SysMailRecord;

public interface SysMailRecordMapper {
    int deleteByPrimaryKey(Long recordId);

    int insert(SysMailRecord record);

    int insertSelective(SysMailRecord record);

    SysMailRecord selectByPrimaryKey(Long recordId);

    int updateByPrimaryKeySelective(SysMailRecord record);

    int updateByPrimaryKey(SysMailRecord record);
}