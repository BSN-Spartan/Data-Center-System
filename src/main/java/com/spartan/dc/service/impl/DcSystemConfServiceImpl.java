package com.spartan.dc.service.impl;

import com.spartan.dc.dao.write.DcSystemConfMapper;
import com.spartan.dc.model.DcSystemConf;
import com.spartan.dc.service.DcSystemConfService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author linzijun
 * @version V1.0
 * @date 2022/12/19 10:49
 */
@Service
public class DcSystemConfServiceImpl implements DcSystemConfService {

    @Resource
    private DcSystemConfMapper dcSystemConfMapper;

    @Override
    public int insertSelective(DcSystemConf record) {
        return dcSystemConfMapper.insertSelective(record);
    }

    @Override
    public int deleteByPrimaryKey(Long confId) {
        return dcSystemConfMapper.deleteByPrimaryKey(confId);
    }

    @Override
    public DcSystemConf querySystemConfByCode(String confCode) {
        return dcSystemConfMapper.querySystemConfByCode(confCode);
    }

    @Override
    public int updateByPrimaryKey(DcSystemConf record) {
        return dcSystemConfMapper.updateByPrimaryKeySelective(record);
    }

}
