package com.spartan.dc.dao.write;


import com.spartan.dc.model.SysRoleResource;

import java.util.List;

public interface SysRoleResourceMapper {
    int deleteByPrimaryKey(Long roleResourceId);

    int insert(SysRoleResource record);

    int insertSelective(SysRoleResource record);

    SysRoleResource selectByPrimaryKey(Long roleResourceId);

    int updateByPrimaryKeySelective(SysRoleResource record);

    int updateByPrimaryKey(SysRoleResource record);

    void batchInsertRoleResource(List<SysRoleResource> list);

    void removeByRoleId(Long roleId);
}