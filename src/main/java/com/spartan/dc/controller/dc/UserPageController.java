package com.spartan.dc.controller.dc;

import com.spartan.dc.config.interceptor.RequiredPermission;
import com.spartan.dc.model.DcUser;
import com.spartan.dc.service.DcUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/user/")
public class UserPageController {

    private final static Logger logger = LoggerFactory.getLogger(UserPageController.class);

    @Autowired
    private DcUserService dcUserService;



    @RequiredPermission(isPage = true)
    @RequestMapping(value = "check/{userId}")
    public String check(@PathVariable("userId") String userIdStr) {

        Long userId = null;
        try {
            userId = Long.parseLong(userIdStr);
        } catch (NumberFormatException e) {
            return "redirect:/user";
        }

        DcUser dcUser = dcUserService.selectByPrimaryKey(userId);

        if (dcUser == null) {
            return "redirect:/user";
        }
        return "/user/detail";
    }

    @RequiredPermission(isPage = true)
    @RequestMapping(value = "edit/{userId}")
    public String edit(@PathVariable("userId") String userIdStr) {

        Long userId = null;
        try {
            userId = Long.parseLong(userIdStr);
        } catch (NumberFormatException e) {
            return "redirect:/user";
        }

        DcUser dcUser = dcUserService.selectByPrimaryKey(userId);

        if (dcUser == null) {
            return "redirect:/user";
        }
        return "/user/edit";
    }



    @RequiredPermission(isPage = true)
    @RequestMapping(value = "add")
    public String add() {

        return "/user/add";
    }


    @RequiredPermission(isPage = true)
    @RequestMapping(value = "resetPwd")
    public String resetPwd() {
        return "/user/resetPwd";
    }

}
