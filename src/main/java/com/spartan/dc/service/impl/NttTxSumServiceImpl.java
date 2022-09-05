package com.spartan.dc.service.impl;

import com.spartan.dc.dao.write.NttTxSumMapper;
import com.spartan.dc.model.vo.resp.NttTxSumRespVO;
import com.spartan.dc.service.NttTxSumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author wzy
 * @create 2022/8/23 14:28
 * @description NttTxSum service
 */
@Service
public class NttTxSumServiceImpl implements NttTxSumService {

    @Autowired
    private NttTxSumMapper nttTxSumMapper;


    @Override
    public NttTxSumRespVO getNTTDealsSummary() {
        return nttTxSumMapper.getNTTDealsSummary();
    }
}
