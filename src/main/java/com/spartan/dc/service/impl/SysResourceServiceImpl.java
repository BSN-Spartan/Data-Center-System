package com.spartan.dc.service.impl;


import com.spartan.dc.core.util.treeview.TreeViewUtil;
import com.spartan.dc.core.util.treeview.Treeview;
import com.spartan.dc.dao.write.SysResourceMapper;
import com.spartan.dc.model.SysResource;
import com.spartan.dc.service.SysResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;


@Service
public class SysResourceServiceImpl implements SysResourceService {

    @Autowired
    private SysResourceMapper sysResourceMapper;

    @Override
    public List<SysResource> listResourceByRoleId(Long roleId) {
        return sysResourceMapper.listResourceByRoleId(roleId);
    }

    @Override
    public List<Treeview> selectAllTreeView() {
        List<SysResource> list = listAll();
        return TreeViewUtil.getTreeviewData(list, 0L, null);
    }

    @Override
    public List<Treeview> getAllByRoleId(Long roleId) {
        List<SysResource> list = listAll();

        List<SysResource> selectResourceList = listResourceByRoleId(roleId);
        List<Long> selectId = null;
        if (!CollectionUtils.isEmpty(selectResourceList)) {
            selectId = new ArrayList<>(selectResourceList.size());
            for (SysResource temp : selectResourceList) {
                selectId.add(temp.getRsucId());
            }
        }

        return TreeViewUtil.getTreeviewData(list, 0L, selectId);
    }

    @Override
    public List<Treeview> getByRoleId(Long roleId) {
        List<SysResource> list = listResourceByRoleId(roleId);
        return TreeViewUtil.getTreeviewData(list, 0L, null);
    }

    private List<SysResource> listAll() {
        return sysResourceMapper.listAll();
    }
}
