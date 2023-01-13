package com.spartan.dc.service;


import com.spartan.dc.core.datatables.DataTable;
import com.spartan.dc.model.SysRole;
import com.spartan.dc.model.SysRoleResource;

import java.util.List;
import java.util.Map;


public interface SysRoleService {

    Map<String, Object> queryRoleList(DataTable<Map<String, Object>> dataTable);

    List<SysRole> listUserRole(Long userId);

    int updateByPrimaryKeySelective(SysRole sysRole);

    SysRole selectByPrimaryKey(Long roleId);

    Map<String, Object> getRoleById(Long roleId);

    List<SysRole> listAll();

    int insertRoleAngResource(SysRole role, SysRoleResource[] roleResource);

    int updateRoleAngResource(SysRole role, SysRoleResource[] roleResource);

    Long countByRoleName(SysRole role);
}
