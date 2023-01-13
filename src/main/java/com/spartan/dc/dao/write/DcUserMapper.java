package com.spartan.dc.dao.write;

import com.spartan.dc.model.DcUser;
import com.spartan.dc.model.SysUserPrincipal;
import com.spartan.dc.model.SysUserRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface DcUserMapper {

    int deleteByPrimaryKey(Long userId);

    int insert(DcUser record);

    int insertSelective(DcUser record);

    DcUser selectByPrimaryKey(Long userId);

    int updateByPrimaryKeySelective(DcUser record);

    int updateByPrimaryKey(DcUser record);

    List<Map<String, Object>> queryUserList(Map<String, Object> condition);

    DcUser selectByEmail(String email);

    DcUser selectUser(@Param("email") String email,@Param("name") String name);

    int updateByPrimaryKeySelective(SysUserPrincipal record);

    void insertUserRole(List<SysUserRole> list);

    Map<String, Object> getUserInfo(Long userId);

    List<DcUser> listByUserId(Long userId);

}