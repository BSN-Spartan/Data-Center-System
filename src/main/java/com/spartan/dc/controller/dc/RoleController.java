package com.spartan.dc.controller.dc;


import com.spartan.dc.config.interceptor.RequiredPermission;
import com.spartan.dc.core.datatables.DataTable;
import com.spartan.dc.core.dto.ResultInfo;
import com.spartan.dc.core.dto.ResultInfoUtil;
import com.spartan.dc.core.enums.SysRoleStateEnum;
import com.spartan.dc.core.exception.GlobalException;
import com.spartan.dc.core.util.json.JsonUtil;
import com.spartan.dc.model.SysRole;
import com.spartan.dc.model.SysRoleResource;
import com.spartan.dc.service.SysResourceService;
import com.spartan.dc.service.SysRoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("sys/role/")
public class RoleController {

    private final static Logger logger = LoggerFactory.getLogger(RoleController.class);

    @Autowired
    private SysRoleService sysRoleService;

    @Autowired
    private SysResourceService sysResourceService;

    @RequiredPermission
    @PostMapping(value = "queryRoleList")
    public ResultInfo queryRoleList(@RequestBody DataTable<Map<String, Object>> dataTable) {

        logger.info("Get data of the role......");

        Map<String, Object> map = sysRoleService.queryRoleList(dataTable);

        return ResultInfoUtil.successResult(map);
    }

    @PostMapping(value = "updateRoleState")
    public ResultInfo updateRoleState(@RequestBody SysRole sysRole) {

        logger.info("Edit user status......");

        if (sysRole == null) {
            return ResultInfoUtil.errorResult("Role information cannot be empty");
        }
        if (sysRole.getRoleId() == null) {
            return ResultInfoUtil.errorResult("Role information cannot be empty");
        }
        if (sysRole.getState() == null) {
            return ResultInfoUtil.errorResult("Role status ID cannot be empty");
        }

        String stateName = SysRoleStateEnum.getMsg(sysRole.getState());
        if (stateName == null) {
            return ResultInfoUtil.errorResult("Invalid role status ID");
        }

        int count = sysRoleService.updateByPrimaryKeySelective(sysRole);
        if (count > 0) {
            return ResultInfoUtil.successResult("Modified successfully");
        }

        return ResultInfoUtil.errorResult("Failed to modify");
    }


    @RequestMapping(value = "get/{roleId}")
    public ResultInfo get(@PathVariable("roleId") String roleIdStr) {
        logger.info("Get role information");

        Long roleId = null;
        try {
            roleId = Long.parseLong(roleIdStr);
        } catch (NumberFormatException e) {
            logger.error("Failed to get the role information, the parameter of 【RoleId】 is incorrect, 【{}】", roleIdStr);
            throw new GlobalException("System error");
        }

        Map<String, Object> role = sysRoleService.getRoleById(roleId);

        Map<String, Object> map = new HashMap<>();
        map.put("role", role);

        // List<SysResource> resourceList = sysResourceService.listResourceByRoleId(roleId);
        // map.put("resourceList",resourceList);

        return ResultInfoUtil.successResult(map);

    }

    @RequestMapping(value = "getAll")
    public ResultInfo getAll() {
        logger.info("Get all role information");

        List<SysRole> list = sysRoleService.listAll();

        return ResultInfoUtil.successResult(list);

    }

    @PostMapping(value = "insertRole")
    @ResponseBody
    public ResultInfo insertRole(@RequestBody Map<String, Object> map) {

        SysRole role = JsonUtil.fromJson(JsonUtil.toJson(map.get("role")), SysRole.class);
        SysRoleResource[] roleResource = JsonUtil.fromJson(JsonUtil.toJson(map.get("roleResource")), SysRoleResource[].class);
        try {

            Long count = sysRoleService.countByRoleName(role);
            if (count != null && count > 0) {
                return ResultInfoUtil.errorResult("Role name already exists");
            }

            int i = sysRoleService.insertRoleAngResource(role, roleResource);
            if (i > 0) {
                return ResultInfoUtil.successResult("The role has been added successfully");
            } else {
                return ResultInfoUtil.errorResult("Failed to add the role");
            }
        } catch (GlobalException e) {
            logger.error("Failed to add the role", e);
            return ResultInfoUtil.errorResult(e.getMessage());
        }

    }

    @PostMapping(value = "updateRole")
    @ResponseBody
    public ResultInfo updateRole(@RequestBody Map<String, Object> map) {

        SysRole role = JsonUtil.fromJson(JsonUtil.toJson(map.get("role")), SysRole.class);
        SysRoleResource[] roleResource = JsonUtil.fromJson(JsonUtil.toJson(map.get("roleResource")), SysRoleResource[].class);
        try {

            Long count = sysRoleService.countByRoleName(role);
            if (count != null && count > 0) {
                return ResultInfoUtil.errorResult("Role name already exists");
            }

            int i = sysRoleService.updateRoleAngResource(role, roleResource);
            if (i > 0) {
                return ResultInfoUtil.successResult("The role has been modified successfully");
            } else {
                return ResultInfoUtil.errorResult("Failed to modify the role");
            }
        } catch (GlobalException e) {
            logger.error("Failed to modify the role", e);
            return ResultInfoUtil.errorResult(e.getMessage());
        }

    }

}
