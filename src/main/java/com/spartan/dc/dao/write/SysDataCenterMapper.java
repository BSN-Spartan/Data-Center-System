package com.spartan.dc.dao.write;

import com.spartan.dc.core.dto.dc.DataCenter;
import com.spartan.dc.model.SysDataCenter;
import org.apache.ibatis.annotations.Param;

public interface SysDataCenterMapper {

    int deleteByPrimaryKey(Long dcId);

    int insert(SysDataCenter record);

    int insertSelective(SysDataCenter record);

    SysDataCenter selectByPrimaryKey(Long dcId);

    int updateByPrimaryKeySelective(SysDataCenter record);

    int updateByPrimaryKey(SysDataCenter record);

    DataCenter getDataCenter();

    SysDataCenter getSysDataCenter();

    SysDataCenter getDcByEmail(@Param("contactsEmail") String contactsEmail);


}