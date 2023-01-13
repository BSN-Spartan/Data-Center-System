package com.spartan.dc.service;


import com.spartan.dc.core.util.treeview.Treeview;
import com.spartan.dc.model.SysResource;

import java.util.List;

public interface SysResourceService {

    List<SysResource> listResourceByRoleId(Long roleId);

    List<Treeview> selectAllTreeView();

    List<Treeview> getAllByRoleId(Long roleId);

    List<Treeview> getByRoleId(Long roleId);
}
