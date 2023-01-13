package com.spartan.dc.dao.write;

import com.spartan.dc.model.SysUserPrincipal;

import java.util.List;
import java.util.Map;

public interface SysUserPrincipalMapper {
    int deleteByPrimaryKey(Long userId);

    int insert(SysUserPrincipal record);

    int insertSelective(SysUserPrincipal record);

    SysUserPrincipal selectByPrimaryKey(Long userId);

    int updateByPrimaryKeySelective(SysUserPrincipal record);

    int updateByPrimaryKey(SysUserPrincipal record);

    List<SysUserPrincipal> getByUser(SysUserPrincipal user);

    List<Map<String,Object>> queryUserList(Map<String, Object> condition);

    Map<String,Object> getUserInfo(Long userId);

    Integer countUser(SysUserPrincipal user);

    int insertUser(SysUserPrincipal user);

}