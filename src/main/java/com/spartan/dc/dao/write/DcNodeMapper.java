package com.spartan.dc.dao.write;

import com.spartan.dc.model.DcNode;

import java.util.List;
import java.util.Map;

public interface DcNodeMapper {
    int deleteByPrimaryKey(Long nodeId);

    int insert(DcNode record);

    int insertSelective(DcNode record);

    DcNode selectByPrimaryKey(Long nodeId);

    int updateByPrimaryKeySelective(DcNode record);

    int updateByPrimaryKey(DcNode record);

    /**
     * Query node list.
     * @param condition
     * @return
     */
    List<Map<String, Object>> queryNodeList(Map<String, Object> condition);

    /**
     *
     * @return
     */
    List<DcNode> getStayNodeUpChainList();


    DcNode getOneByNodeID(String nodeID);

}