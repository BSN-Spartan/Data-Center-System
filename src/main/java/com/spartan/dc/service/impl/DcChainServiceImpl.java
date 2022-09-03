package com.spartan.dc.service.impl;

import com.github.pagehelper.PageHelper;
import com.spartan.dc.core.datatables.DataTable;
import com.spartan.dc.dao.write.DcChainMapper;
import com.spartan.dc.model.DcChain;
import com.spartan.dc.service.DcChainService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Desc：
 *
 * @Created by 2022-07-16 20:29
 */
@Service
public class DcChainServiceImpl implements DcChainService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DcChainMapper dcChainMapper;


    @Override
    public Map<String, Object> queryChainList(DataTable<Map<String, Object>> dataTable) {

        PageHelper.startPage(dataTable.getParam().getPageIndex(), dataTable.getParam().getPageSize());

        List<Map<String, Object>> list = dcChainMapper.queryChainList(dataTable.getCondition());

        return dataTable.getReturnData(list);
    }

    @Override
    public DcChain selectByPrimaryKey(Long chainId) {
        return dcChainMapper.selectByPrimaryKey(chainId);
    }

    @Override
    public List<DcChain> getOpbChainList() {

        List<DcChain> list = dcChainMapper.getOpbChainList();

        return list;
    }

}
