package com.spartan.dc.service.impl;

import com.github.pagehelper.PageHelper;
import com.spartan.dc.core.conf.SystemConf;
import com.spartan.dc.core.datatables.DataTable;
import com.spartan.dc.core.dto.user.AddUserReqVO;
import com.spartan.dc.core.dto.user.ModifyPassReqVO;
import com.spartan.dc.core.dto.user.UpdateUserStateReqVO;
import com.spartan.dc.core.dto.user.UserLoginReqVO;
import com.spartan.dc.core.exception.GlobalException;
import com.spartan.dc.core.util.common.CryptoUtils;
import com.spartan.dc.core.util.common.Md5Utils;
import com.spartan.dc.core.util.common.UUIDUtil;
import com.spartan.dc.core.util.user.UserGlobals;
import com.spartan.dc.core.util.user.UserLoginInfo;
import com.spartan.dc.dao.write.DcUserMapper;
import com.spartan.dc.model.DcUser;
import com.spartan.dc.service.DcUserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Desc：
 *
 * @Created by 2022-07-16 15:49
 */
@Service
public class DcUserServiceImpl implements DcUserService {

    @Autowired
    private DcUserMapper dcUserMapper;


    @Autowired
    private SystemConf systemConf;

    @Override
    public UserLoginInfo handleLogin(UserLoginReqVO userLoginReqVO) {

        String email = null;
        String pwd = null;
        try {
            email = CryptoUtils.getStringDecrypt(userLoginReqVO.getEmail());
            pwd = CryptoUtils.getStringDecrypt(userLoginReqVO.getPassword());
        } catch (Exception e) {
            throw new GlobalException("Password decryption failed");
        }
        if (StringUtils.isEmpty(email) || StringUtils.isEmpty(pwd)) {
            throw new GlobalException("Username/Password is incorrect");
        }

        DcUser dcUser = dcUserMapper.selectByEmail(email);
        if (dcUser == null) {
            throw new GlobalException("Username/Password is incorrect");
        }

        String salt = dcUser.getSalt();
        String encryptPass = encryptPass(salt, pwd);
        if (!Objects.equals(encryptPass, dcUser.getPassword())) {
            throw new GlobalException("Username/Password is incorrect");
        }

        if (dcUser.getState() == 0) {
            throw new GlobalException("The user is disabled.");
        }


        UserLoginInfo userLoginInfo = new UserLoginInfo();
        userLoginInfo.setUserName(dcUser.getContactsName());
        userLoginInfo.setUserId(dcUser.getUserId());
        userLoginInfo.setEmail(email);
        userLoginInfo.setSystemName(systemConf.getName());
        userLoginInfo.setSystemIcon(systemConf.getIcon());
        userLoginInfo.setSystemLogo(systemConf.getLogo());

        return userLoginInfo;
    }

    @Override
    public UserLoginInfo handleLogin() {
        UserLoginInfo userLoginInfo = new UserLoginInfo();
        userLoginInfo.setSystemName(systemConf.getName());
        userLoginInfo.setSystemIcon(systemConf.getIcon());
        userLoginInfo.setSystemLogo(systemConf.getLogo());
        return userLoginInfo;
    }

    @Override
    public Map<String, Object> queryUserList(DataTable<Map<String, Object>> dataTable) {

        PageHelper.startPage(dataTable.getParam().getPageIndex(), dataTable.getParam().getPageSize());

        List<Map<String, Object>> list = dcUserMapper.queryUserList(dataTable.getCondition());

        return dataTable.getReturnData(list);
    }

    @Override
    public Integer addUser(AddUserReqVO addUserReqVO) {
        if (Objects.isNull(addUserReqVO)) {
            throw new GlobalException("user information can not be empty");
        }
        DcUser user = dcUserMapper.selectUser(addUserReqVO.getEmail(), addUserReqVO.getUserName());
        if (user != null) {
            throw new GlobalException("Email or name already registered");
        }
        DcUser dcUser = new DcUser();
        dcUser.setContactsEmail(addUserReqVO.getEmail());
        dcUser.setContactsName(addUserReqVO.getUserName());
        String salt = UUIDUtil.generate().toLowerCase();
        dcUser.setSalt(salt);
        String encryptPass = encryptPass(salt, addUserReqVO.getPassword());
        dcUser.setPassword(encryptPass);
        dcUser.setContactsPhone(addUserReqVO.getPhone());
        dcUser.setState(Short.valueOf("1"));
        dcUser.setCreateTime(new Date());

        return dcUserMapper.insert(dcUser);
    }

    @Override
    public Integer updateUserState(UpdateUserStateReqVO updateUserStateReqVO) {
        DcUser dcUser = new DcUser();
        dcUser.setUserId(updateUserStateReqVO.getUserId());
        dcUser.setState(Short.valueOf(updateUserStateReqVO.getUserState()));

        int count = dcUserMapper.updateByPrimaryKeySelective(dcUser);

        return count;
    }

    @Override
    public Integer modifyPass(ModifyPassReqVO modifyPassReqVO, Long userId, HttpSession session) {

        DcUser dcUser = dcUserMapper.selectByPrimaryKey(userId);
        if (Objects.isNull(dcUser)) {
            throw new GlobalException("User does not exist");
        }
        String oldEncryptPass = encryptPass(dcUser.getSalt(), modifyPassReqVO.getOldPassword());
        if (!Objects.equals(oldEncryptPass, dcUser.getPassword())) {
            throw new GlobalException("Incorrect password");
        }


        String salt = UUIDUtil.generate().toLowerCase();
        String newEncryptPass = encryptPass(salt, modifyPassReqVO.getNewPassword());
        dcUser.setSalt(salt);
        dcUser.setPassword(newEncryptPass);
        int count = dcUserMapper.updateByPrimaryKeySelective(dcUser);


        if (count > 0) {
            session.removeAttribute(UserGlobals.USER_SESSION_KEY);
        }

        return count;
    }


    private static String encryptPass(String salt, String password) {
        try {
            return Md5Utils.encodeUtf8(salt + password);
        } catch (Exception e) {
            throw new GlobalException("Failed to encrypt the password");
        }
    }
}
