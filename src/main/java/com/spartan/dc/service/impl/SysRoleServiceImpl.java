package com.spartan.dc.service.impl;


import com.github.pagehelper.PageHelper;
import com.spartan.dc.core.datatables.DataTable;
import com.spartan.dc.core.enums.SysRoleStateEnum;
import com.spartan.dc.dao.write.SysRoleMapper;
import com.spartan.dc.dao.write.SysRoleResourceMapper;
import com.spartan.dc.model.SysRole;
import com.spartan.dc.model.SysRoleResource;
import com.spartan.dc.service.SysRoleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class SysRoleServiceImpl implements SysRoleService {

    @Autowired
    private SysRoleMapper sysRoleMapper;
    @Autowired
    private SysRoleResourceMapper sysRoleResourceMapper;


    @Override
    public Map<String, Object> queryRoleList(DataTable<Map<String, Object>> dataTable) {
        PageHelper.startPage(dataTable.getParam().getPageIndex(), dataTable.getParam().getPageSize());

        List<SysRole> list = sysRoleMapper.queryRoleList(dataTable.getCondition());

        return dataTable.getReturnData(list);
    }

    @Override
    public List<SysRole> listUserRole(Long userId) {
        return sysRoleMapper.listUserRole(userId);
    }

    @Override
    public int updateByPrimaryKeySelective(SysRole sysRole) {
        return sysRoleMapper.updateByPrimaryKeySelective(sysRole);
    }

    @Override
    public SysRole selectByPrimaryKey(Long roleId) {
        return sysRoleMapper.selectByPrimaryKey(roleId);
    }

    @Override
    public Map<String, Object> getRoleById(Long roleId) {
        return sysRoleMapper.getRoleById(roleId);
    }

    @Override
    public List<SysRole> listAll() {
        return sysRoleMapper.listAll();
    }

    @Override
    public int insertRoleAngResource(SysRole role, SysRoleResource[] roleResource) {
        role.setCreateDate(new Date());
        role.setCreateUser(0L);
        role.setState(SysRoleStateEnum.ABLE.getCode());
        role.setUpdateUser(0L);
        role.setUpdateDate(new Date());

        String roleCode = sysRoleMapper.selectNewRoleCode();
        String newRoleCode = "R0001";
        if (StringUtils.isNotEmpty(roleCode)) {
            newRoleCode = String.format("R" + "%0" + 4 + "d", Integer.parseInt(roleCode.substring(1, roleCode.length())) + 1);
        }
        role.setRoleCode(newRoleCode);
        int count = sysRoleMapper.insertRole(role);

        List<SysRoleResource> list = new ArrayList<>(roleResource.length);
        SysRoleResource sysRoleResource = null;

        for (int i = 0; i < roleResource.length; i++) {
            sysRoleResource = new SysRoleResource();
            sysRoleResource.setRoleId(role.getRoleId());
            sysRoleResource.setRsucId(roleResource[i].getRsucId());
            list.add(sysRoleResource);
        }
        sysRoleResourceMapper.batchInsertRoleResource(list);
        return count;
    }

    @Override
    public int updateRoleAngResource(SysRole role, SysRoleResource[] roleResource) {
        role.setUpdateUser(0L);
        role.setUpdateDate(new Date());

        int count = sysRoleMapper.updateByPrimaryKeySelective(role);

        List<SysRoleResource> list = new ArrayList<>(roleResource.length);
        SysRoleResource sysRoleResource = null;

        for (int i = 0; i < roleResource.length; i++) {
            sysRoleResource = new SysRoleResource();
            sysRoleResource.setRoleId(role.getRoleId());
            sysRoleResource.setRsucId(roleResource[i].getRsucId());
            list.add(sysRoleResource);
        }
        sysRoleResourceMapper.removeByRoleId(role.getRoleId());
        sysRoleResourceMapper.batchInsertRoleResource(list);
        return count;
    }

    @Override
    public Long countByRoleName(SysRole role) {
        return sysRoleMapper.countByRoleName(role);
    }
}
