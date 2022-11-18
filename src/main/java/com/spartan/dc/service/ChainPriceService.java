package com.spartan.dc.service;

import com.spartan.dc.core.datatables.DataTable;
import com.spartan.dc.model.vo.req.AddChainSalePriceReqVO;

import java.util.Map;

/**
 * @author wxq
 * @create 2022/8/8 18:11
 * @description Charge manager service.
 */
public interface ChainPriceService {

    /**
     * Query chain price list
     *
     * @param dataTable
     * @return chain price list
     */
    Map<String, Object> queryChainSalePriceList(DataTable<Map<String, Object>> dataTable);

    Boolean addPrice(AddChainSalePriceReqVO vo);

    // Get price details
    Map<String, Object> getSalePriceDetail(Integer salePriceId);

    // Review price information
    Boolean toAudit(String checkRemark, Short checkResult, Integer salePriceId);


}
