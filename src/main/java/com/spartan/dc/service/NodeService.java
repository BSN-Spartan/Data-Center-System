package com.spartan.dc.service;

import com.spartan.dc.core.datatables.DataTable;
import com.spartan.dc.model.vo.req.AddDcNodeReqVO;
import com.spartan.dc.model.vo.req.OffNetworkReqVO;
import org.web3j.crypto.Credentials;

import java.util.Map;

/**
 * @author wxq
 * @create 2022/8/15 13:21
 * @description node manager interface
 */
public interface NodeService {
    /**
     * Query node list
     *
     * @param dataTable
     * @return charge list
     */
    Map<String, Object> queryNodeList(DataTable<Map<String, Object>> dataTable);


    /**
     * offNetwork
     *
     * @param vo
     * @return
     */
    boolean offNetwork(OffNetworkReqVO vo);

    /**
     * add datacenter node
     *
     * @param dcNode
     * @return
     * @throws Exception
     */
    boolean addNode(AddDcNodeReqVO dcNode, Credentials credentials) throws Exception;
}
