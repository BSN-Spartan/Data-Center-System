package com.spartan.dc.service;

import com.spartan.dc.core.datatables.DataTable;
import com.spartan.dc.model.DcTermsServiceAudit;
import com.spartan.dc.model.vo.resp.TermsServiceDetailRespVO;

import java.util.Map;

/**
 * @author linzijun
 * @version V1.0
 * @date 2023/2/15 11:01
 */
public interface TermsServiceAuditService {

    int insertSelective(DcTermsServiceAudit record);

    DcTermsServiceAudit selectByPrimaryKey(Long serviceAuditId);

    int updateByPrimaryKeySelective(DcTermsServiceAudit record);

    Boolean checkUnreviewed();

    TermsServiceDetailRespVO detail(Long serviceAuditId);

    Map<String, Object> queryTermsServiceAuditList(DataTable<Map<String, Object>> dataTable);

    String queryNewestTreaty();
}
