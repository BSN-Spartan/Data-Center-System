package com.spartan.dc.service.impl;

import com.github.pagehelper.PageHelper;
import com.spartan.dc.core.datatables.DataTable;
import com.spartan.dc.dao.write.DcTermsServiceAuditMapper;
import com.spartan.dc.model.DcTermsServiceAudit;
import com.spartan.dc.model.vo.resp.TermsServiceDetailRespVO;
import com.spartan.dc.service.TermsServiceAuditService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author linzijun
 * @version V1.0
 * @date 2023/2/15 11:03
 */
@Service
public class TermsServiceAuditServiceImpl implements TermsServiceAuditService {

    @Resource
    private DcTermsServiceAuditMapper dcTermsServiceAuditMapper;

    @Override
    public int insertSelective(DcTermsServiceAudit record) {
        return dcTermsServiceAuditMapper.insertSelective(record);
    }

    @Override
    public DcTermsServiceAudit selectByPrimaryKey(Long serviceAuditId) {
        return dcTermsServiceAuditMapper.selectByPrimaryKey(serviceAuditId);
    }

    @Override
    public int updateByPrimaryKeySelective(DcTermsServiceAudit record) {
        return dcTermsServiceAuditMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public Boolean checkUnreviewed() {
        DcTermsServiceAudit dcTermsServiceAudit = dcTermsServiceAuditMapper.getUnreviewed();
        if (dcTermsServiceAudit != null) {
            return true;
        }
        return false;
    }

    @Override
    public TermsServiceDetailRespVO detail(Long serviceAuditId) {
        return dcTermsServiceAuditMapper.detail(serviceAuditId);
    }

    @Override
    public Map<String, Object> queryTermsServiceAuditList(DataTable<Map<String, Object>> dataTable) {

        PageHelper.startPage(dataTable.getParam().getPageIndex(), dataTable.getParam().getPageSize());

        Map<String, Object> condition = dataTable.getCondition();

        List<Map<String, Object>> list = dcTermsServiceAuditMapper.queryTermsServiceAuditList(condition);

        return dataTable.getReturnData(list);
    }

    @Override
    public String queryNewestTreaty() {
        DcTermsServiceAudit dcTermsServiceAudit = dcTermsServiceAuditMapper.queryNewestTreaty();
        if (dcTermsServiceAudit != null) {
            return dcTermsServiceAudit.getTermsContent();
        }
        return "";
    }
}
