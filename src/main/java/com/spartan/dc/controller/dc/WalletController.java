package com.spartan.dc.controller.dc;

import com.spartan.dc.config.interceptor.RequiredPermission;
import com.spartan.dc.core.dto.ResultInfo;
import com.spartan.dc.core.dto.ResultInfoUtil;
import com.spartan.dc.model.vo.req.GenerateNewWalletReqVO;
import com.spartan.dc.service.WalletService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.web3j.utils.Strings;


/**
 * @author wxq
 * @create 2022/8/18 18:56
 * @description wallet controller
 */
@RestController
@RequestMapping("wallet/")
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

}
