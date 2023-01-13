package com.spartan.dc.dao.write;



import com.spartan.dc.model.SysRole;

import java.util.List;
import java.util.Map;

public interface SysRoleMapper {
    int deleteByPrimaryKey(Long roleId);

    int insert(SysRole record);

    int insertSelective(SysRole record);

    SysRole selectByPrimaryKey(Long roleId);

    int updateByPrimaryKeySelective(SysRole record);

    int updateByPrimaryKey(SysRole record);

    List<SysRole> listUserRole(Long userId);

    List<SysRole> queryRoleList(Map<String, Object> condition);

    Map<String, Object> getRoleById(Long roleId);

    List<SysRole> listAll();

    Long countByRoleName(SysRole role);

    String selectNewRoleCode();

    int insertRole(SysRole role);
}