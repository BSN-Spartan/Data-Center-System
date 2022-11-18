package com.spartan.dc.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.reddate.spartan.constant.ErrorMessage;
import com.reddate.spartan.exception.SpartanException;
import com.spartan.dc.core.util.common.CacheManager;
import com.spartan.dc.core.util.user.UserGlobals;
import com.spartan.dc.core.util.user.UserLoginInfo;
import com.spartan.dc.service.WalletService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.web3j.crypto.Credentials;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpSession;

import static com.spartan.dc.core.util.common.CacheManager.PASSWORD_CACHE_KEY;

/**
 * @Author : rjx
 * @Date : 2022/7/28 16:54
 **/
@ApiIgnore
public class BaseController {

    @Autowired
    private WalletService walletService;

    public UserLoginInfo getUserInfo(HttpSession session) {
        Object userInfoObj = session.getAttribute(UserGlobals.USER_SESSION_KEY);
        UserLoginInfo userLoginInfo = JSONObject.parseObject(JSON.toJSONString(userInfoObj), UserLoginInfo.class);
        return userLoginInfo;
    }


    protected Credentials getCredentials(String password) {
        //
        String pwd = Strings.isBlank(password) ? CacheManager.get(PASSWORD_CACHE_KEY) : password;
        if (org.web3j.utils.Strings.isEmpty(pwd)) {
            throw new SpartanException(ErrorMessage.IS_NULL, "keystore password");
        }
        //
        try {
            return walletService.loadWallet(pwd);
        } catch (Exception e) {
            throw new SpartanException(ErrorMessage.UNKNOWN_ERROR.getCode(), e.getMessage());
        }
    }

}
