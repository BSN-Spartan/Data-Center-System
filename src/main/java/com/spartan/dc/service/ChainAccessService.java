package com.spartan.dc.service;

import com.spartan.dc.core.dto.dc.DataCenter;
import com.spartan.dc.model.vo.req.AddChainAccessReqVO;
import com.spartan.dc.model.vo.req.AddDataCenterReqVO;

/**
 * Desc：
 *
 * @Created by 2022-07-16 19:44
 */
public interface ChainAccessService {

    boolean addChainAccess(AddChainAccessReqVO addChainAccessReqVO);

}
