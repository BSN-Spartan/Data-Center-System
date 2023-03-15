package com.spartan.dc.service.impl;

import com.spartan.dc.dao.write.DcGasRechargeRecordMapper;
import com.spartan.dc.model.DcGasRechargeRecord;
import com.spartan.dc.model.vo.resp.RechargeDetailRespVO;
import com.spartan.dc.service.DcGasRechargeRecordService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author linzijun
 * @version V1.0
 * @date 2023/2/14 13:46
 */
@Service
public class DcGasRechargeRecordServiceImpl implements DcGasRechargeRecordService {

    @Resource
    private DcGasRechargeRecordMapper dcGasRechargeRecordMapper;

    @Override
    public DcGasRechargeRecord selectByPrimaryKey(Long rechargeRecordId) {
        return dcGasRechargeRecordMapper.selectByPrimaryKey(rechargeRecordId);
    }

    @Override
    public int updateByPrimaryKeySelective(DcGasRechargeRecord record) {
        return dcGasRechargeRecordMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public RechargeDetailRespVO rechargeDetail(Long rechargeRecordId) {
        return dcGasRechargeRecordMapper.rechargeDetail(rechargeRecordId);
    }
}
