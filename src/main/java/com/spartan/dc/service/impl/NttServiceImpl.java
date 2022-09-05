package com.spartan.dc.service.impl;

import com.github.pagehelper.PageHelper;
import com.spartan.dc.core.datatables.DataTable;
import com.spartan.dc.dao.write.NttTxRecordMapper;
import com.spartan.dc.model.vo.resp.NttRewardRespVO;
import com.spartan.dc.service.NttService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author wxq
 * @create 2022/8/11 14:00
 * @description ntt service
 */
@Service
public class NttServiceImpl implements NttService {
    @Autowired
    private NttTxRecordMapper nttTxRecordMapper;


    @Override
    public Map<String, Object> queryNttList(DataTable<Map<String, Object>> dataTable) {

        PageHelper.startPage(dataTable.getParam().getPageIndex(), dataTable.getParam().getPageSize());

        List<Map<String, Object>> list = nttTxRecordMapper.queryNttList(dataTable.getCondition());

        return dataTable.getReturnData(list);
    }


    @Override
    public NttRewardRespVO getNTTRecentAward() {
        return nttTxRecordMapper.getNTTRecentAward();
    }
}
