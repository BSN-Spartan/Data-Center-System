package com.spartan.dc.service.impl;

import com.github.pagehelper.PageHelper;
import com.spartan.dc.core.datatables.DataTable;
import com.spartan.dc.core.dto.ResultInfo;
import com.spartan.dc.core.dto.ResultInfoUtil;
import com.spartan.dc.core.easyexcel.DcPaymentOrderExcelEntity;
import com.spartan.dc.core.easyexcel.DcPaymentRefundExcelEntity;
import com.spartan.dc.core.enums.*;
import com.spartan.dc.core.exception.GlobalException;
import com.spartan.dc.core.util.bean.BeanUtils;
import com.spartan.dc.core.util.common.FileCode;
import com.spartan.dc.core.util.common.UUIDUtil;
import com.spartan.dc.core.util.excel.HutoolExcelUtils;
import com.spartan.dc.core.vo.req.*;
import com.spartan.dc.core.vo.resp.DcPaymentOrderDetailsRespVO;
import com.spartan.dc.core.vo.resp.DcPaymentOrderExcelRespVO;
import com.spartan.dc.core.vo.resp.DcPaymentRefundExcelRespVO;
import com.spartan.dc.core.vo.resp.DcSystemConfRespVO;
import com.spartan.dc.dao.write.*;
import com.spartan.dc.model.*;
import com.spartan.dc.model.vo.req.RemittanceRegisterReqVO;
import com.spartan.dc.model.vo.resp.DcPaymentOrderRespVO;
import com.spartan.dc.service.DcAccountBalanceConfService;
import com.spartan.dc.service.DcPaymentOrderService;
import com.spartan.dc.service.DcPaymentTypeService;
import com.spartan.dc.service.DcbackGroundSystemService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName DcbackGroundSystemServicelmpl
 * @Author wjx
 * @Date 2022/11/3 20:15
 * @Version 1.0
 */

@Service
@Slf4j
public class DcAccountBalanceConfServicelmpl extends BaseService implements DcAccountBalanceConfService {

    @Resource
    private SysDataCenterMapper sysDataCenterMapper;

    @Resource
    private DcAccountBalanceConfMapper dcAccountBalanceConfMapper;

    @Override
    public List<DcAccountBalanceConf> queryAccountMonitor() {
        SysDataCenter sysDataCenter = sysDataCenterMapper.getSysDataCenter();
        if (sysDataCenter == null) {
            throw new GlobalException("Data center information is not configured");
        }

        DcAccountBalanceConf queryParams = DcAccountBalanceConf.builder().
                chainAddress(sysDataCenter.getNttAccountAddress())
                .build();

        List<DcAccountBalanceConf> dcAccountBalanceConfList = dcAccountBalanceConfMapper.getReminderAccountById(queryParams);
        return dcAccountBalanceConfList;
    }

    @Override
    public ResultInfo updateAccountMonitor(List<DcAccountBalanceConf> DcAccountBalanceConfList) {

        SysDataCenter sysDataCenter = sysDataCenterMapper.getSysDataCenter();
        if (sysDataCenter == null) {
            throw new GlobalException("Data center information is not configured");
        }

        for (DcAccountBalanceConf updateConf:DcAccountBalanceConfList) {
            DcAccountBalanceConf queryParams = DcAccountBalanceConf.builder().
                    chainAddress(sysDataCenter.getNttAccountAddress())
                    .monitorType(updateConf.getMonitorType())
                    .build();

            List<DcAccountBalanceConf> dcAccountBalanceConfList = dcAccountBalanceConfMapper.getReminderAccountById(queryParams);

            if(CollectionUtils.isEmpty(dcAccountBalanceConfList)){
                updateConf.setChainAddress(sysDataCenter.getNttAccountAddress());
                updateConf.setChainId(Long.valueOf(1));
                dcAccountBalanceConfMapper.insertSelective(updateConf);
            }else{
                updateConf.setAccountBalanceConfId(dcAccountBalanceConfList.get(0).getAccountBalanceConfId());
                dcAccountBalanceConfMapper.updateByPrimaryKeySelective(updateConf);
            }

        }


        return ResultInfoUtil.successResult("Success");
    }


}
