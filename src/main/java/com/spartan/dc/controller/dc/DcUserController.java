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
import com.spartan.dc.core.util.user.UserGlobals;
import com.spartan.dc.core.util.user.UserLoginInfo;
import com.spartan.dc.service.DcUserService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Map;


@RestController
@RequestMapping("sys/user/")
public class DcUserController extends BaseController {

    private final static Logger logger = LoggerFactory.getLogger(DcUserController.class);

    @Autowired
    private DcUserService dcUserService;

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
    public ResultInfo addUser(@RequestBody AddUserReqVO addUserReqVO) {
        return ResultInfoUtil.successResult(dcUserService.addUser(addUserReqVO));
    }

    @PostMapping(value = "updateUserState")
    public ResultInfo updateUserState(@RequestBody UpdateUserStateReqVO updateUserStateReqVO) {
        return ResultInfoUtil.successResult(dcUserService.updateUserState(updateUserStateReqVO));
    }

    @PostMapping(value = "modifyPass")
    public ResultInfo modifyPass(@RequestBody ModifyPassReqVO modifyPassReqVO, HttpSession session) {
        UserLoginInfo userInfo = this.getUserInfo(session);
        return ResultInfoUtil.successResult(dcUserService.modifyPass(modifyPassReqVO, userInfo.getUserId(), session));
    }
}
