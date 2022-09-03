package com.spartan.dc.service;

import com.spartan.dc.core.datatables.DataTable;
import com.spartan.dc.model.vo.resp.NttRewardRespVO;

import java.util.Map;

/**
 * @author wxq
 * @create 2022/8/11 14:00
 * @description ntt service
 */
public interface NttService {
    /**
     * Query ntt list
     * @param dataTable
     * @return charge list
     */
    Map<String, Object> queryNttList(DataTable<Map<String, Object>> dataTable);



    NttRewardRespVO getNTTRecentAward();

}
