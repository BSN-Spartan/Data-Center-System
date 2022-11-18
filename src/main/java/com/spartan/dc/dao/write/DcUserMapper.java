package com.spartan.dc.dao.write;

import com.spartan.dc.model.DcUser;
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

    DcUser getUserByToken(String token);

    List<Map<String, Object>> queryUserList(Map<String, Object> condition);

    DcUser selectByEmail(String email);

    DcUser selectUser(@Param("email") String email,@Param("name") String name);
}