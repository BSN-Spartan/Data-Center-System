package com.spartan.dc.service.impl;

import com.reddate.spartan.SpartanSdkClient;
import com.reddate.spartan.net.SpartanGovern;
import com.spartan.dc.core.dto.dc.DataCenter;
import com.spartan.dc.core.exception.GlobalException;
import com.spartan.dc.core.util.common.CacheManager;
import com.spartan.dc.dao.write.SysDataCenterMapper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.web3j.crypto.Credentials;
import org.web3j.utils.Strings;

import java.util.Objects;

import static com.spartan.dc.core.util.common.CacheManager.PASSWORD_CACHE_KEY;

/**
 * @author wxq
 * @create 2022/8/10 15:52
 * @description base service
 */
@Component
public class BaseService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    protected SpartanSdkClient spartanSdkClient;


    @Autowired
    private SysDataCenterMapper sysDataCenterMapper;

    @Value("${chain.chainId}")
    public int chainId;

    public boolean checkPasswordExpired() {
        String cacheValue = CacheManager.get(PASSWORD_CACHE_KEY);
        return StringUtils.isNotBlank(cacheValue);
    }

    public String checkDataCenterInfo(String keystorePassword, Credentials credentials) {
        // check keystore info
        DataCenter dataCenter = sysDataCenterMapper.getDataCenter();
        if (Objects.isNull(dataCenter)) {
            throw new GlobalException("The basic information of data center is not configured");
        }

        // check password
        if (Objects.nonNull(credentials)) {
            if (!credentials.getAddress().equalsIgnoreCase(dataCenter.getNttAccountAddress())) {
                throw new GlobalException("Keystore and data center do not match");
            }
            if (StringUtils.isNotBlank(keystorePassword)) {
                CacheManager.put(PASSWORD_CACHE_KEY, keystorePassword);
            } else {
                String value = CacheManager.get(PASSWORD_CACHE_KEY);
                if (StringUtils.isBlank(value)) {
                    throw new GlobalException("Keystore password has expired");
                }
                CacheManager.put(PASSWORD_CACHE_KEY, value);
            }

            if (Strings.isEmpty(SpartanGovern.getNonceManagerAddress())) {
                SpartanGovern.setNonceManagerAddress(credentials.getAddress());
            }
        } else {
            if (StringUtils.isBlank(CacheManager.get(PASSWORD_CACHE_KEY))) {
                throw new GlobalException("Keystore password has expired");
            }
        }
        return dataCenter.getToken();
    }

}
