package com.spartan.dc.dao.write;



import com.spartan.dc.model.SysResource;

import java.util.List;

public interface SysResourceMapper {
    int deleteByPrimaryKey(Long rsucId);

    int insert(SysResource record);

    int insertSelective(SysResource record);

    SysResource selectByPrimaryKey(Long rsucId);

    int updateByPrimaryKeySelective(SysResource record);

    int updateByPrimaryKey(SysResource record);

    List<SysResource> listResourceByRoleId(Long roleId);

    List<SysResource> listAll();

    List<SysResource> listByUserId(Long userId);
}