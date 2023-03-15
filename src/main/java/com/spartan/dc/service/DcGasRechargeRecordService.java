package com.spartan.dc.service;

import com.spartan.dc.model.DcGasRechargeRecord;
import com.spartan.dc.model.vo.resp.RechargeDetailRespVO;

/**
 * @author linzijun
 * @version V1.0
 * @date 2023/2/14 13:44
 */
public interface DcGasRechargeRecordService {

    DcGasRechargeRecord selectByPrimaryKey(Long rechargeRecordId);

    int updateByPrimaryKeySelective(DcGasRechargeRecord record);

    RechargeDetailRespVO rechargeDetail(Long rechargeRecordId);
}
