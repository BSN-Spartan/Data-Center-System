package com.spartan.dc.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.reddate.spartan.SpartanSdkClient;
import com.reddate.spartan.net.SpartanGovern;
import com.spartan.dc.core.dto.dc.DataCenter;
import com.spartan.dc.core.exception.GlobalException;
import com.spartan.dc.core.util.common.CacheManager;
import com.spartan.dc.core.util.user.UserGlobals;
import com.spartan.dc.core.util.user.UserLoginInfo;
import com.spartan.dc.dao.write.SysDataCenterMapper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.web3j.crypto.Credentials;
import org.web3j.utils.Strings;

import javax.servlet.http.HttpSession;
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

    public UserLoginInfo getUserInfo() {
        try {
            HttpSession session = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getSession();
            Object userInfoObj = session.getAttribute(UserGlobals.USER_SESSION_KEY);
            UserLoginInfo userLoginInfo = JSONObject.parseObject(JSON.toJSONString(userInfoObj), UserLoginInfo.class);
            return userLoginInfo;
        } catch (Exception e) {
            return null;
        }
    }

    public Long getUserId() {
        UserLoginInfo userInfo = getUserInfo();
        if (userInfo == null) {
            return 0L;
        }
        return userInfo.getUserId();
    }

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
