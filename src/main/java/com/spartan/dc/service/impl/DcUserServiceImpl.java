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
import com.spartan.dc.dao.write.SysResourceMapper;
import com.spartan.dc.dao.write.SysUserRoleMapper;
import com.spartan.dc.model.DcUser;
import com.spartan.dc.model.SysResource;
import com.spartan.dc.model.SysRole;
import com.spartan.dc.model.SysUserRole;
import com.spartan.dc.service.DcUserService;
import com.spartan.dc.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.*;

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
    private SysResourceMapper sysResourceMapper;

    @Resource
    private SystemConf systemConf;

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Autowired
    private SysRoleService sysRoleService;

    @Value("${system.defaultPassword}")
    private String defaultPassword;

    @Override
    public UserLoginInfo handleLogin(UserLoginReqVO userLoginReqVO) {
        String email;
        String pwd;
        try {
            email = CryptoUtils.getStringDecrypt(userLoginReqVO.getEmail());
            pwd = CryptoUtils.getStringDecrypt(userLoginReqVO.getPassword());
        } catch (Exception e) {
            throw new GlobalException("Password decryption failed");
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

        List<SysResource> resourceList = sysResourceMapper.listByUserId( dcUser.getUserId());
        userLoginInfo.setResourceList(resourceList);
        if (CollectionUtils.isEmpty(resourceList)) {
            throw new GlobalException("No permission, please contact the administrator");
        }

        String rsucUrl = resourceList.stream()
                .filter(sysResource -> !Objects.equals(sysResource.getRsucUrl(), "javascript:;"))
                .findFirst().get().getRsucUrl();
        userLoginInfo.setSuccessUrl(rsucUrl);

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
    public Integer addUser(AddUserReqVO addUserReqVO, SysUserRole[] userRole) {
        if (Objects.isNull(addUserReqVO)) {
            throw new GlobalException("user information can not be empty");
        }
        String userName;
        String password;
        String email;
        try {
            userName = CryptoUtils.getStringDecrypt(addUserReqVO.getUserName());
            password = CryptoUtils.getStringDecrypt(addUserReqVO.getPassword());
            email = CryptoUtils.getStringDecrypt(addUserReqVO.getEmail());
        } catch (Exception e) {
            throw new GlobalException("Password decryption failed");
        }

        DcUser user = dcUserMapper.selectUser(email, userName);
        if (user != null) {
            throw new GlobalException("Email or name already registered");
        }

        DcUser dcUser = new DcUser();
        dcUser.setContactsEmail(email);
        dcUser.setContactsName(userName);
        String salt = UUIDUtil.generate().toLowerCase();
        dcUser.setSalt(salt);
        password = defaultPassword;
        String encryptPass = encryptPass(salt, password);
        dcUser.setPassword(encryptPass);
        dcUser.setContactsPhone(addUserReqVO.getPhone());
        dcUser.setState(Short.valueOf("1"));
        dcUser.setCreateTime(new Date());
        int insert = dcUserMapper.insert(dcUser);

        List<SysUserRole> list = new ArrayList<>(userRole.length);
        SysUserRole sysUserRole = null;
        for (int i = 0; i < userRole.length; i++) {
            sysUserRole = new SysUserRole();
            sysUserRole.setUserId(dcUser.getUserId());
            sysUserRole.setRoleId(userRole[i].getRoleId());
            list.add(sysUserRole);
        }
        sysUserRoleMapper.insertUserRole(list);

        return insert;
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
        String oldPassword;
        String newPassword;
        try {
            oldPassword = CryptoUtils.getStringDecrypt(modifyPassReqVO.getOldPassword());
            newPassword = CryptoUtils.getStringDecrypt(modifyPassReqVO.getNewPassword());
        } catch (Exception e) {
            throw new GlobalException("Password decryption failed");
        }

        DcUser dcUser = dcUserMapper.selectByPrimaryKey(userId);
        if (Objects.isNull(dcUser)) {
            throw new GlobalException("User does not exist");
        }
        String oldEncryptPass = encryptPass(dcUser.getSalt(), oldPassword);
        if (!Objects.equals(oldEncryptPass, dcUser.getPassword())) {
            throw new GlobalException("Incorrect password");
        }

        String salt = UUIDUtil.generate().toLowerCase();
        String newEncryptPass = encryptPass(salt, newPassword);
        dcUser.setSalt(salt);
        dcUser.setPassword(newEncryptPass);
        int count = dcUserMapper.updateByPrimaryKeySelective(dcUser);

        if (count > 0) {
            session.removeAttribute(UserGlobals.USER_SESSION_KEY);
        }
        return count;
    }

    @Override
    public DcUser selectByPrimaryKey(Long userId) {
        return dcUserMapper.selectByPrimaryKey(userId);
    }


    private static String encryptPass(String salt, String password) {
        try {
            return Md5Utils.encodeUtf8(salt + password);
        } catch (Exception e) {
            throw new GlobalException("Failed to encrypt the password");
        }
    }

    @Override
    public int editUserAndRole(DcUser user, SysUserRole[] userRole) {
        int count = dcUserMapper.updateByPrimaryKeySelective(user);

        sysUserRoleMapper.removeByUserId(user.getUserId());

        List<SysUserRole> list = new ArrayList<>(userRole.length);
        SysUserRole sysUserRole = null;
        for (int i = 0; i < userRole.length; i++) {
            sysUserRole = new SysUserRole();
            sysUserRole.setUserId(user.getUserId());
            sysUserRole.setRoleId(userRole[i].getRoleId());
            list.add(sysUserRole);
        }
        dcUserMapper.insertUserRole(list);

        return count;
    }

    @Override
    public void insertUserRole(List<SysUserRole> list) {

    }
    @Override
    public Map<String, Object> getUserInfo(Long userId) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> user = dcUserMapper.getUserInfo(userId);
        map.put("user", user);
        List<SysRole> roleList = sysRoleService.listUserRole(userId);
        map.put("roleList", roleList);
        return map;
    }

    @Override
    public int resetPassWord(DcUser dcUser) {
        dcUser = dcUserMapper.selectByPrimaryKey(dcUser.getUserId());

        String salt = UUIDUtil.generate().toLowerCase();
        try {
            dcUser.setSalt(salt);
            dcUser.setPassword(encryptPass(salt, defaultPassword));
            return updateByPrimaryKeySelective(dcUser);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    public int updateByPrimaryKeySelective(DcUser dcUser) {
        return dcUserMapper.updateByPrimaryKeySelective(dcUser);
    }
}
