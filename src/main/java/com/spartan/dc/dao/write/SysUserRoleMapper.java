package com.spartan.dc.dao.write;



import com.spartan.dc.model.SysUserRole;

import java.util.List;

public interface SysUserRoleMapper {
    int deleteByPrimaryKey(Long userRoleId);

    int insert(SysUserRole record);

    int insertSelective(SysUserRole record);

    SysUserRole selectByPrimaryKey(Long userRoleId);

    int updateByPrimaryKeySelective(SysUserRole record);

    int updateByPrimaryKey(SysUserRole record);

    void insertUserRole(List<SysUserRole> list);

    void removeByUserId(Long userId);
}