package com.spartan.dc.service.impl;

import java.util.Date;
import java.util.List;

import com.spartan.dc.core.dto.dc.DataCenter;
import com.spartan.dc.core.exception.GlobalException;
import com.spartan.dc.core.util.common.*;
import com.spartan.dc.dao.write.SysDataCenterMapper;
import com.spartan.dc.model.SysDataCenter;
import com.spartan.dc.model.vo.req.AddDataCenterReqVO;
import com.spartan.dc.service.SysDataCenterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * Desc：
 *
 * @Created by 2022-07-16 19:44
 */
@Service
public class SysDataCenterServiceImpl implements SysDataCenterService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${chain.walletFilePath}")
    public String walletFilePath;

    @Autowired
    private SysDataCenterMapper sysDataCenterMapper;

    @Override
    public DataCenter get() {
        return sysDataCenterMapper.getDataCenter();
    }

    @Override
    public boolean addDataCenter(AddDataCenterReqVO vo) {
        DataCenter dataCenter = sysDataCenterMapper.getDataCenter();
        if (dataCenter != null) {
            throw new GlobalException("Data center already exists");
        }
        List<String> walletFileName = FileUtil.queryFileNamesWithoutSuffix(walletFilePath);
        if (!CollectionUtils.isEmpty(walletFileName)) {
            if (walletFileName.size() > 1) {
                throw new GlobalException("Keystore cannot be configured multiple times");
            }
            if (!walletFileName.get(0).equalsIgnoreCase(vo.getNttAccountAddress())) {
                throw new GlobalException("NTT wallet and the configured keystore do not match");
            }
        }

        SysDataCenter dc = new SysDataCenter();
        dc.setContactsEmail(vo.getEmail());
        dc.setCreateTime(new Date());
        dc.setToken(vo.getToken());
        dc.setDcCode(vo.getDcCode());
        dc.setNttAccountAddress(vo.getNttAccountAddress());
        int count = sysDataCenterMapper.insert(dc);
        return count > 0;
    }


}
