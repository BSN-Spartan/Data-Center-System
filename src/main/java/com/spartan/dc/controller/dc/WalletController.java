package com.spartan.dc.controller.dc;

import com.reddate.spartan.SpartanSdkClient;
import com.spartan.dc.config.interceptor.RequiredPermission;
import com.spartan.dc.core.conf.EventBlockConf;
import com.spartan.dc.core.dto.ResultInfo;
import com.spartan.dc.core.dto.ResultInfoUtil;
import com.spartan.dc.dao.write.EventBlockMapper;
import com.spartan.dc.model.EventBlock;
import com.spartan.dc.model.vo.req.GenerateNewWalletReqVO;
import com.spartan.dc.service.SysDataCenterService;
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
import org.web3j.utils.Strings;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.concurrent.atomic.AtomicLong;

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

    @Resource
    private SpartanSdkClient spartanSdkClient;

    @Resource
    private EventBlockMapper eventBlockMapper;

    @Autowired
    private SysDataCenterService sysDataCenterService;

    @RequiredPermission
    @PostMapping(value = "generateNewWalletFile")
    public ResultInfo generateNewWalletFile(@RequestBody @Validated GenerateNewWalletReqVO vo) throws Exception {
        if (Strings.isEmpty(vo.getPassword())) {
            return ResultInfoUtil.errorResult("Password cannot be empty");
        }

        String fileName = walletService.generateNewWalletFile(vo.getPassword(), vo.getPrivateKey());

        if (!Strings.isEmpty(fileName)) {

//            // Get the latest block height
//            if (sysDataCenterService.get() != null) {
//                BigInteger blockNumber = spartanSdkClient.baseService.getBlockNumber();
//                EventBlockConf.eventBlock = new AtomicLong(blockNumber.longValue());
//                EventBlock eventBlock = new EventBlock();
//                eventBlock.setBlockHeight(blockNumber.longValue());
//                eventBlockMapper.updateEventBlock(eventBlock);
//            }

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
