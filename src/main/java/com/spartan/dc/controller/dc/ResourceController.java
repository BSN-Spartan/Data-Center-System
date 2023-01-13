package com.spartan.dc.controller.dc;


import com.spartan.dc.core.dto.ResultInfo;
import com.spartan.dc.core.dto.ResultInfoUtil;
import com.spartan.dc.core.exception.GlobalException;
import com.spartan.dc.core.util.treeview.Treeview;
import com.spartan.dc.service.SysResourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("sys/resource/")
public class ResourceController {

    private final static Logger logger = LoggerFactory.getLogger(ResourceController.class);

    @Autowired
    private SysResourceService sysResourceService;


    @RequestMapping(value = "getAll")
    public ResultInfo getAll() {
        List<Treeview> resourceList = sysResourceService.selectAllTreeView();

        return ResultInfoUtil.successResult(resourceList);

    }


    @RequestMapping(value = "getAllByRoleId/{roleId}")
    public ResultInfo getAllByRoleId(@PathVariable("roleId") String roleIdStr) {
        Long roleId = null;
        try {
            roleId = Long.parseLong(roleIdStr);
        } catch (NumberFormatException e) {
            logger.error("Failed to get the role information, the parameter of 【RoleId】 is incorrect, 【{}】", roleIdStr);
            throw new GlobalException("System error");
        }

        List<Treeview> resourceList = sysResourceService.getAllByRoleId(roleId);

        return ResultInfoUtil.successResult(resourceList);

    }
    @RequestMapping(value = "getByRoleId/{roleId}")
    public ResultInfo getByRoleId(@PathVariable("roleId") String roleIdStr) {
        Long roleId = null;
        try {
            roleId = Long.parseLong(roleIdStr);
        } catch (NumberFormatException e) {
            logger.error("Failed to get the role information, the parameter of 【RoleId】 is incorrect, 【{}】", roleIdStr);
            throw new GlobalException("System error");
        }

        List<Treeview> resourceList = sysResourceService.getByRoleId(roleId);

        return ResultInfoUtil.successResult(resourceList);

    }

}
