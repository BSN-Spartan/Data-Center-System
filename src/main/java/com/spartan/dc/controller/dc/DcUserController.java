package com.spartan.dc.controller.dc;

import com.spartan.dc.config.interceptor.RequiredPermission;
import com.spartan.dc.controller.BaseController;
import com.spartan.dc.core.datatables.DataTable;
import com.spartan.dc.core.dto.ResultInfo;
import com.spartan.dc.core.dto.ResultInfoUtil;
import com.spartan.dc.core.dto.user.AddUserReqVO;
import com.spartan.dc.core.dto.user.ModifyPassReqVO;
import com.spartan.dc.core.dto.user.UpdateUserStateReqVO;
import com.spartan.dc.core.dto.user.UserLoginReqVO;
import com.spartan.dc.core.exception.GlobalException;
import com.spartan.dc.core.util.json.JsonUtil;
import com.spartan.dc.core.util.user.UserGlobals;
import com.spartan.dc.core.util.user.UserLoginInfo;
import com.spartan.dc.model.DcUser;
import com.spartan.dc.model.SysUserRole;
import com.spartan.dc.service.DcUserService;
import com.spartan.dc.service.SysRoleService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpSession;
import java.util.Map;


@RestController
@RequestMapping("sys/user/")
@ApiIgnore
public class DcUserController extends BaseController {

    private final static Logger logger = LoggerFactory.getLogger(DcUserController.class);

    @Autowired
    private DcUserService dcUserService;

    @Autowired
    private SysRoleService sysRoleService;

    @Value("${spring.profiles.active}")
    private String activeType;


    @PostMapping(value = "login")
    @ResponseBody
    public ResultInfo login(@RequestBody UserLoginReqVO userLoginReqVO, HttpSession session) {
        logger.info("User......Login......");
        // check code
        if (StringUtils.isEmpty(userLoginReqVO.getCode())) {
            return ResultInfoUtil.errorResult("The verification code cannot be empty");
        }

        if (StringUtils.isEmpty(userLoginReqVO.getEmail())) {
            return ResultInfoUtil.errorResult("The mailbox cannot be empty");
        }

        if (StringUtils.isEmpty(userLoginReqVO.getPassword())) {
            return ResultInfoUtil.errorResult("The password cannot be empty");
        }

        Object code = session.getAttribute("code");
        if (code == null) {
            return ResultInfoUtil.errorResult("Verification code has expired");
        }

        if (!code.toString().equalsIgnoreCase(userLoginReqVO.getCode())) {
            return ResultInfoUtil.errorResult("Incorrect verification code");
        }

        try {
            UserLoginInfo userLoginInfo = dcUserService.handleLogin(userLoginReqVO);
            if (userLoginInfo != null) {


                session.setAttribute(UserGlobals.USER_SESSION_KEY, userLoginInfo);

                session.setAttribute(UserGlobals.SHOW_REMINDER_KEY, true);

                return ResultInfoUtil.successResult(userLoginInfo);
            } else {
                return ResultInfoUtil.errorResult("Login failed");
            }
        } catch (GlobalException e) {
            throw e;
        }

    }


    @RequestMapping(value = "getUserResource")
    public ResultInfo getUserResource(HttpSession session) {
        logger.info("Obtain user rights information");

        UserLoginInfo userLoginInfo = (UserLoginInfo) session.getAttribute(UserGlobals.USER_SESSION_KEY);

        return ResultInfoUtil.successResult(userLoginInfo);
    }

    @RequestMapping(value = "getUserResourceWithoutLogin")
    public ResultInfo getUserResourceWithoutLogin() {
        logger.info("Obtain user rights information");
        UserLoginInfo userLoginInfo = dcUserService.handleLogin();

        return ResultInfoUtil.successResult(userLoginInfo);
    }

    @GetMapping(value = "logout")
    @ResponseBody
    public ResultInfo logout(HttpSession session) {
        logger.info("Logout......");
        if (session.getAttribute(UserGlobals.USER_SESSION_KEY) != null) {
            session.setAttribute(UserGlobals.USER_SESSION_KEY, null);
        }
        return ResultInfoUtil.successResult("ok");
    }


    @RequiredPermission
    @PostMapping(value = "queryUserList")
    public ResultInfo queryUserList(@RequestBody DataTable<Map<String, Object>> dataTable) {

        logger.info("Obtaining User Data......");

        Map<String, Object> map = dcUserService.queryUserList(dataTable);

        return ResultInfoUtil.successResult(map);
    }

    @PostMapping(value = "addUser")
    public ResultInfo addUser(@RequestBody Map<String, Object> map) {
        AddUserReqVO addUserReqVO = JsonUtil.fromJson(JsonUtil.toJson(map.get("user")), AddUserReqVO.class);
        SysUserRole[] userRole = JsonUtil.fromJson(JsonUtil.toJson(map.get("userRole")), SysUserRole[].class);

        return ResultInfoUtil.successResult(dcUserService.addUser(addUserReqVO, userRole));
    }

    @PostMapping(value = "updateUserState")
    public ResultInfo updateUserState(@RequestBody UpdateUserStateReqVO updateUserStateReqVO) {
        return ResultInfoUtil.successResult(dcUserService.updateUserState(updateUserStateReqVO));
    }

    @PostMapping(value = "modifyPass")
    public ResultInfo modifyPass(@RequestBody @Validated ModifyPassReqVO modifyPassReqVO, HttpSession session) {
        UserLoginInfo userInfo = this.getUserInfo(session);
        return ResultInfoUtil.successResult(dcUserService.modifyPass(modifyPassReqVO, userInfo.getUserId(), session));
    }


    @RequestMapping(value = "get/{userId}")
    public ResultInfo get(@PathVariable("userId") String userIdStr) {
        Long userId = null;
        try {
            userId = Long.parseLong(userIdStr);
        } catch (NumberFormatException e) {
            throw new GlobalException("System error");
        }

        Map<String, Object> map = dcUserService.getUserInfo(userId);

        return ResultInfoUtil.successResult(map);
    }

    @PostMapping(value = "editUser")
    @ResponseBody
    public ResultInfo editUser(@RequestBody Map<String, Object> map) {
        DcUser user = JsonUtil.fromJson(JsonUtil.toJson(map.get("user")), DcUser.class);
        SysUserRole[] userRole = JsonUtil.fromJson(JsonUtil.toJson(map.get("userRole")), SysUserRole[].class);

        if (user.getUserId() == null) {
            return ResultInfoUtil.errorResult("User information cannot be empty");
        }
        if (StringUtils.isEmpty(user.getContactsName())) {
            return ResultInfoUtil.errorResult("Name cannot be empty");
        }
        if (userRole == null || userRole.length == 0) {
            return ResultInfoUtil.errorResult("The role cannot be empty");
        }
        int i = dcUserService.editUserAndRole(user, userRole);
        if (i > 0) {
            return ResultInfoUtil.successResult("User information has been modified successfully");
        }
        return ResultInfoUtil.errorResult("Failed to modify the user information");
    }

    @PostMapping(value = "resetPassWord")
    public ResultInfo resetPassWord(@RequestBody DcUser dcUser) {



        if (dcUser == null) {
            return ResultInfoUtil.errorResult("User information cannot be empty");
        }
        if (dcUser.getUserId() == null) {
            return ResultInfoUtil.errorResult("User information cannot be empty");
        }


        int count = dcUserService.resetPassWord(dcUser);
        if (count > 0) {
            return ResultInfoUtil.successResult("Your password has been changed successfully");
        }

        return ResultInfoUtil.errorResult("Failed to reset the password");
    }


}
