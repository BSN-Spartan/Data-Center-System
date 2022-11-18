package com.spartan.dc.controller.dc;

import com.reddate.spartan.net.SpartanGovern;
import com.spartan.dc.config.interceptor.RequiredPermission;
import com.spartan.dc.core.dto.ResultInfo;
import com.spartan.dc.core.dto.ResultInfoUtil;
import com.spartan.dc.core.util.common.CacheManager;
import com.spartan.dc.model.vo.req.GenerateNewWalletReqVO;
import com.spartan.dc.model.vo.req.UpdateKeystorePasswordReqVO;
import com.spartan.dc.service.WalletService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.utils.Strings;
import springfox.documentation.annotations.ApiIgnore;

import java.io.IOException;
import java.util.Objects;

import static com.spartan.dc.core.util.common.CacheManager.PASSWORD_CACHE_KEY;


/**
 * @author wxq
 * @create 2022/8/18 18:56
 * @description wallet controller
 */
@RestController
@RequestMapping("wallet/")
@ApiIgnore
public class WalletController {
    private final static Logger logger = LoggerFactory.getLogger(WalletController.class);

    @Autowired
    private WalletService walletService;

    @RequiredPermission
    @PostMapping(value = "generateNewWalletFile")
    public ResultInfo generateNewWalletFile(@RequestBody @Validated GenerateNewWalletReqVO vo) throws Exception {
        if (Strings.isEmpty(vo.getPassword())) {
            return ResultInfoUtil.errorResult("Password cannot be empty");
        }

        String fileName = walletService.generateNewWalletFile(vo.getPassword(), vo.getPrivateKey());

        if (!Strings.isEmpty(fileName)) {
            return ResultInfoUtil.successResult(fileName);
        } else {
            return ResultInfoUtil.errorResult("Error generating new wallet file");
        }
    }

    @RequiredPermission
    @PostMapping(value = "checkWalletExists")
    public ResultInfo checkWalletExists() {
        boolean result = walletService.checkWalletExists();
        if (result) {
            return ResultInfoUtil.successResult("true");
        } else {
            return ResultInfoUtil.errorResult("false");
        }
    }

    @RequiredPermission
    @PostMapping(value = "updateKeystorePassword")
    public ResultInfo updateKeystorePassword(@RequestBody @Validated UpdateKeystorePasswordReqVO vo) throws Exception {
        if (Strings.isEmpty(vo.getKeystorePassword())) {
            return ResultInfoUtil.errorResult("Password cannot be empty");
        }
        try {
            Credentials credentials = walletService.loadWallet(vo.getKeystorePassword());
            if (Objects.isNull(credentials)){
                return ResultInfoUtil.errorResult("Invalid password provided");
            }
            CacheManager.put(PASSWORD_CACHE_KEY, vo.getKeystorePassword());
            SpartanGovern.setNonceManagerAddress(credentials.getAddress());
        } catch (Exception e) {
            logger.error("updateKeystorePassword error:", e);
            return ResultInfoUtil.errorResult("Invalid password provided");
        }
        return ResultInfoUtil.successResult();

    }

}
