package com.spartan.dc.dao.write;

import com.spartan.dc.model.DcAccountBalanceConf;
import com.spartan.dc.model.vo.resp.BalanceReminderRespVo;

import java.util.List;

public interface DcAccountBalanceConfMapper {
    int deleteByPrimaryKey(Long accountBalanceConfId);

    int insert(DcAccountBalanceConf record);

    int insertSelective(DcAccountBalanceConf record);

    DcAccountBalanceConf selectByPrimaryKey(Long accountBalanceConfId);

    int updateByPrimaryKeySelective(DcAccountBalanceConf record);

    int updateByPrimaryKey(DcAccountBalanceConf record);

    List<BalanceReminderRespVo> getReminderAccountList();

    List<DcAccountBalanceConf> getReminderAccountById(DcAccountBalanceConf record);


}