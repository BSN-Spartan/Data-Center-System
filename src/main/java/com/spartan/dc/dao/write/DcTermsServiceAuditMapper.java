package com.spartan.dc.dao.write;

import com.spartan.dc.model.DcTermsServiceAudit;
import com.spartan.dc.model.vo.resp.TermsServiceDetailRespVO;

import java.util.List;
import java.util.Map;

public interface DcTermsServiceAuditMapper {

    int deleteByPrimaryKey(Long serviceAuditId);

    int insert(DcTermsServiceAudit record);

    int insertSelective(DcTermsServiceAudit record);

    DcTermsServiceAudit selectByPrimaryKey(Long serviceAuditId);

    int updateByPrimaryKeySelective(DcTermsServiceAudit record);

    int updateByPrimaryKeyWithBLOBs(DcTermsServiceAudit record);

    int updateByPrimaryKey(DcTermsServiceAudit record);

    DcTermsServiceAudit getUnreviewed();

    TermsServiceDetailRespVO detail(Long serviceAuditId);

    List<Map<String, Object>> queryTermsServiceAuditList(Map<String, Object> condition);

    DcTermsServiceAudit queryNewestTreaty();
}