package com.spartan.dc.service;

import com.spartan.dc.core.vo.resp.DcSystemConfRespVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @ClassName PortalParametersService
 * @Author wjx
 * @Date 2022/11/3 17:36
 * @Version 1.0
 */
public interface PortalParametersService {
    List<DcSystemConfRespVO> querySystemConf();

    List<DcSystemConfRespVO> querySystemConfTechnicalSupport();

    List<DcSystemConfRespVO> querySystemConfContactUs();
}
