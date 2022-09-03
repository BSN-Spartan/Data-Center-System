package com.spartan.dc.dao.write;

import com.spartan.dc.model.NttTxSum;
import com.spartan.dc.model.vo.resp.NttTxSumRespVO;

public interface NttTxSumMapper {
    int deleteByPrimaryKey(Long nttTxSumId);

    int insert(NttTxSum record);

    int insertSelective(NttTxSum record);

    NttTxSum selectByPrimaryKey(Long nttTxSumId);

    int updateByPrimaryKeySelective(NttTxSum record);

    int updateByPrimaryKey(NttTxSum record);

    /**
     *
     *
     * @return
     */
    NttTxSumRespVO getNTTDealsSummary();


    NttTxSum getOne();
}