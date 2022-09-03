package com.spartan.dc.service;

import com.spartan.dc.core.datatables.DataTable;
import com.spartan.dc.core.dto.user.AddUserReqVO;
import com.spartan.dc.core.dto.user.ModifyPassReqVO;
import com.spartan.dc.core.dto.user.UpdateUserStateReqVO;
import com.spartan.dc.core.dto.user.UserLoginReqVO;
import com.spartan.dc.core.util.user.UserLoginInfo;

import javax.servlet.http.HttpSession;
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

    Integer addUser(AddUserReqVO addUserReqVO);

    Integer updateUserState(UpdateUserStateReqVO updateUserStateReqVO);

    Integer modifyPass(ModifyPassReqVO modifyPassReqVO, Long userId, HttpSession session);
}
