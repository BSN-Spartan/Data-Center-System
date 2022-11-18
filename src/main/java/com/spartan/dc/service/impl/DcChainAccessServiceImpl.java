package com.spartan.dc.service.impl;

import com.github.pagehelper.PageHelper;
import com.spartan.dc.core.datatables.DataTable;
import com.spartan.dc.core.enums.DcChainAccessStateEnum;
import com.spartan.dc.core.enums.SystemConfCodeEnum;
import com.spartan.dc.dao.write.DcChainAccessMapper;
import com.spartan.dc.dao.write.DcSystemConfMapper;
import com.spartan.dc.model.DcChainAccess;
import com.spartan.dc.model.DcSystemConf;
import com.spartan.dc.service.DcChainAccessService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author linzijun
 * @version V1.0
 * @date 2022/11/7 18:08
 */
@Service
public class DcChainAccessServiceImpl implements DcChainAccessService {

    @Resource
    private DcChainAccessMapper dcChainAccessMapper;

    @Resource
    private DcSystemConfMapper dcSystemConfMapper;

    @Override
    public DcChainAccess selectByPrimaryKey(Long chainAccessId) {
        return dcChainAccessMapper.selectByPrimaryKey(chainAccessId);
    }

    @Override
    public int updateByPrimaryKeySelective(DcChainAccess record) {
        return dcChainAccessMapper.updateByPrimaryKeySelective(record);
    }

    /**
     * Disable currently active
     */
    @Override
    public void disableTheCurrentEnabled() {
        DcChainAccess dcChainAccess = this.getCurrentEnabled();
        if (dcChainAccess != null) {
            dcChainAccess.setState(DcChainAccessStateEnum.BLOCK_UP.getCode());
            dcChainAccess.setUpdateTime(new Date());
            dcChainAccessMapper.updateByPrimaryKeySelective(dcChainAccess);
        }
    }

    /**
     * Get the currently enabled
     * @return
     */
    @Override
    public DcChainAccess getCurrentEnabled() {
        return dcChainAccessMapper.getCurrentEnabled();
    }

    @Override
    public int insertSelective(DcChainAccess record) {
        return dcChainAccessMapper.insertSelective(record);
    }

    @Override
    public Map<String, Object> queryList(DataTable<Map<String, Object>> dataTable) {
        PageHelper.startPage(dataTable.getParam().getPageIndex(), dataTable.getParam().getPageSize());
        List<Map<String, Object>> list = dcChainAccessMapper.queryList(dataTable.getCondition());
        // TPS、TPD
        if (list != null && list.size() > 0) {
            DcSystemConf tpsConf = dcSystemConfMapper.querySystemConfByCode(SystemConfCodeEnum.TPS.getCode());
            DcSystemConf tpdConf = dcSystemConfMapper.querySystemConfByCode(SystemConfCodeEnum.TPD.getName());
            for (Map<String, Object> map : list) {
                map.put("defaultTps", tpsConf.getConfValue());
                map.put("defaultTpd", tpdConf.getConfValue());
            }
        }
        return dataTable.getReturnData(list);
    }
}
