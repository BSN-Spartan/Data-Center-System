package com.spartan.dc.service;


import com.spartan.dc.core.dto.ResultInfo;
import com.spartan.dc.core.vo.req.SysDataCenterTreatyReqVO;
import com.spartan.dc.model.DcAccountBalanceConf;
import java.util.List;

/**
 * @ClassName DcbackGroundSystemService
 * @Author wjx
 * @Date 2022/11/3 20:15
 * @Version 1.0
 */
public interface DcAccountBalanceConfService {


    List<DcAccountBalanceConf> queryAccountMonitor();

    ResultInfo updateAccountMonitor(List<DcAccountBalanceConf> DcAccountBalanceConfList);

}
