package com.spartan.dc.service;

import com.spartan.dc.core.datatables.DataTable;
import com.spartan.dc.core.dto.ResultInfo;
import com.spartan.dc.core.vo.req.DcChainAccessReqVO;
import com.spartan.dc.model.DcChainAccess;

import java.util.Map;

/**
 * @author linzijun
 * @version V1.0
 * @date 2022/11/7 18:07
 */
public interface DcChainAccessService {

    DcChainAccess selectByPrimaryKey(Long chainAccessId);

    int updateByPrimaryKeySelective(DcChainAccess record);

    void disableTheCurrentEnabled();

    DcChainAccess getCurrentEnabled();

    int insertSelective(DcChainAccess record);

    Map<String, Object> queryList(DataTable<Map<String, Object>> dataTable);

    ResultInfo updateAccessInformation(DcChainAccessReqVO dcChainAccessReqVO);
}
