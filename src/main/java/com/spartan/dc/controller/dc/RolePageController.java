package com.spartan.dc.controller.dc;

import com.spartan.dc.config.interceptor.RequiredPermission;
import com.spartan.dc.model.SysRole;
import com.spartan.dc.service.SysRoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("role/")
public class RolePageController {

    private final static Logger logger = LoggerFactory.getLogger(RolePageController.class);

    @Autowired
    private SysRoleService sysRoleService;


    @RequiredPermission(isPage = true)
    @RequestMapping(value = "check/{role}")
    public String check(@PathVariable("role") String roleStr) {
        Long role = null;
        try {
            role = Long.parseLong(roleStr);
        } catch (NumberFormatException e) {
            return "redirect:/role";
        }

        SysRole sysRole = sysRoleService.selectByPrimaryKey(role);

        if (sysRole == null) {
            return "redirect:/role";
        }
        return "role/detail";
    }


    @RequiredPermission(isPage = true)
    @RequestMapping(value = "edit/{role}")
    public String edit(@PathVariable("role") String roleStr) {
        Long role = null;
        try {
            role = Long.parseLong(roleStr);
        } catch (NumberFormatException e) {
            return "redirect:/role";
        }

        SysRole sysRole = sysRoleService.selectByPrimaryKey(role);

        if (sysRole == null) {
            return "redirect:/role";
        }
        return "role/edit";
    }

    @RequiredPermission(isPage = true)
    @RequestMapping(value = "add")
    public String add() {
        return "role/add";
    }


}
