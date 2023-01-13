package com.spartan.dc.service;

import com.spartan.dc.core.datatables.DataTable;
import com.spartan.dc.core.dto.user.AddUserReqVO;
import com.spartan.dc.core.dto.user.ModifyPassReqVO;
import com.spartan.dc.core.dto.user.UpdateUserStateReqVO;
import com.spartan.dc.core.dto.user.UserLoginReqVO;
import com.spartan.dc.core.util.user.UserLoginInfo;
import com.spartan.dc.model.DcUser;
import com.spartan.dc.model.SysUserRole;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

/**
 * Desc：
 *
 * @Created by 2022-07-16 15:39
 */
public interface DcUserService {

    UserLoginInfo handleLogin(UserLoginReqVO userLoginReqVO);
    UserLoginInfo handleLogin();

    Map<String, Object> queryUserList(DataTable<Map<String, Object>> dataTable);

    Integer addUser(AddUserReqVO addUserReqVO, SysUserRole[] userRole);

    Integer updateUserState(UpdateUserStateReqVO updateUserStateReqVO);

    Integer modifyPass(ModifyPassReqVO modifyPassReqVO, Long userId, HttpSession session);

    DcUser selectByPrimaryKey(Long userId);

    int editUserAndRole(DcUser user, SysUserRole[] userRole);

    void insertUserRole(List<SysUserRole> list);

    int resetPassWord(DcUser dcUser);

    Map<String, Object> getUserInfo(Long userId);
}
