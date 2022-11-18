package com.spartan.dc.service.impl;

import com.spartan.dc.core.enums.DcSystemConfTypeEnum;
import com.spartan.dc.core.enums.SystemConfCodeEnum;
import com.spartan.dc.core.vo.resp.DcSystemConfRespVO;
import com.spartan.dc.dao.write.DcSystemConfMapper;
import com.spartan.dc.dao.write.SysDataCenterMapper;
import com.spartan.dc.model.SysDataCenter;
import com.spartan.dc.service.PortalParametersService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName PortalParametersServicelmpl
 * @Author wjx
 * @Date 2022/11/3 17:36
 * @Version 1.0
 */

@Service
public class PortalParametersServicelmpl implements PortalParametersService {
    @Resource
    private DcSystemConfMapper dcSystemConfMapper;
    @Resource
    private SysDataCenterMapper sysDataCenterMapper;

    @Value("${system.iconBase64}")
    private String iconBase64;

    @Override
    public List<DcSystemConfRespVO> querySystemConf() {
        List<DcSystemConfRespVO> dcSystemConfRespVOS = dcSystemConfMapper.querySystemConf(DcSystemConfTypeEnum.PORTAL_INFORMATION.getCode());
        SysDataCenter sysDataCenter = sysDataCenterMapper.getSysDataCenter();
        for (DcSystemConfRespVO dcSystemConfRespVO : dcSystemConfRespVOS) {
            if (dcSystemConfRespVO.getConfCode().equals(SystemConfCodeEnum.LOGO.getCode())) {
                if (StringUtils.isNotBlank(sysDataCenter.getLogo())) {
                    dcSystemConfRespVO.setConfValue(sysDataCenter.getLogo());
                } else {
                    dcSystemConfRespVO.setConfValue(iconBase64);
                }
            }

        }
        return dcSystemConfRespVOS;
    }

    @Override
    public List<DcSystemConfRespVO> querySystemConfTechnicalSupport() {
        return dcSystemConfMapper.querySystemConf(DcSystemConfTypeEnum.TECHNICAL_SUPPORT.getCode());
    }

    @Override
    public List<DcSystemConfRespVO> querySystemConfContactUs() {
        return dcSystemConfMapper.querySystemConf(DcSystemConfTypeEnum.CONTACT_US.getCode());
    }
}
