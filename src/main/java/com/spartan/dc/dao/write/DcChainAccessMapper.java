package com.spartan.dc.dao.write;

import com.spartan.dc.model.DcChainAccess;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface DcChainAccessMapper {
    int deleteByPrimaryKey(Long chainAccessId);

    int insert(DcChainAccess record);

    int insertSelective(DcChainAccess record);

    DcChainAccess selectByPrimaryKey(Long chainAccessId);

    int updateByPrimaryKeySelective(DcChainAccess record);

    int updateByPrimaryKey(DcChainAccess record);

    DcChainAccess getCurrentEnabled();
	
	DcChainAccess selectByEmail(String email);

    List<Map<String, Object>> queryList(Map<String, Object> map);

    DcChainAccess queryChainAccessState(String email);
}