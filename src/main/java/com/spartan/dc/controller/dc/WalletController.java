package com.spartan.dc.controller.dc;

import com.reddate.spartan.net.SpartanGovern;
import com.spartan.dc.config.interceptor.RequiredPermission;
import com.spartan.dc.core.dto.ResultInfo;
import com.spartan.dc.core.dto.ResultInfoUtil;
import com.spartan.dc.core.enums.DcSystemConfTypeEnum;
import com.spartan.dc.core.enums.SystemConfCodeEnum;
import com.spartan.dc.core.enums.WhetherPromptImportEnum;
import com.spartan.dc.core.util.common.CacheManager;
import com.spartan.dc.core.util.user.UserGlobals;
import com.spartan.dc.core.vo.req.SetWhetherHintImportReqVO;
import com.spartan.dc.model.DcSystemConf;
import com.spartan.dc.model.vo.req.GenerateNewWalletReqVO;
import com.spartan.dc.model.vo.req.UpdateKeystorePasswordReqVO;
import com.spartan.dc.service.DcSystemConfService;
import com.spartan.dc.service.WalletService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
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

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Date;
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

    @Autowired
    private DcSystemConfService dcSystemConfService;

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

    /**
     * Do you need the reminder of entering the keystore password?
     * @return
     */
    @ApiOperation(value = "Do you need the reminder of entering the keystore password?")
    @PostMapping(value = "promptInputKeystore")
    public ResultInfo<Boolean> promptInputKeystore(HttpSession session) {

        if (Objects.nonNull(session.getAttribute(UserGlobals.SHOW_REMINDER_KEY)) && !(boolean)session.getAttribute(UserGlobals.SHOW_REMINDER_KEY)) {
            session.setAttribute(UserGlobals.SHOW_REMINDER_KEY, false);
            return ResultInfoUtil.successResult(false);
        }

        // Check whether the reminder of entering the keystore password has been disabled.
        DcSystemConf dcSystemConf = dcSystemConfService.querySystemConfByCode(SystemConfCodeEnum.NO_MORE_PROMPTS_INPUT.getCode());
        if (Objects.nonNull(dcSystemConf) && Objects.equals("1", dcSystemConf.getConfValue())) {
            session.setAttribute(UserGlobals.SHOW_REMINDER_KEY, false);
            return ResultInfoUtil.successResult(false);
        }

        // Check whether the keystore password has been configured.
        Boolean walletExists = walletService.checkWalletExists();
        if (!walletExists) {
            session.setAttribute(UserGlobals.SHOW_REMINDER_KEY, false);
            return ResultInfoUtil.successResult(true);
        }

        if (StringUtils.isEmpty(CacheManager.get(PASSWORD_CACHE_KEY))) {
            session.setAttribute(UserGlobals.SHOW_REMINDER_KEY, false);
            return ResultInfoUtil.successResult(true);
        }

        session.setAttribute(UserGlobals.SHOW_REMINDER_KEY, false);
        return ResultInfoUtil.successResult(false);
    }

    /**
     * Set whether to ask for entering the keystore password
     * @return
     */
    @ApiOperation(value = "Set whether to ask for entering the keystore password")
    @PostMapping("setWhetherHintImport")
    public ResultInfo setWhetherHintImport(@RequestBody SetWhetherHintImportReqVO setWhetherHintImportReqVO) {
        DcSystemConf dcSystemConf = dcSystemConfService.querySystemConfByCode(SystemConfCodeEnum.NO_MORE_PROMPTS_INPUT.getCode());

        if (Objects.isNull(dcSystemConf)) {
            dcSystemConf = DcSystemConf.builder()
                    .confCode(SystemConfCodeEnum.NO_MORE_PROMPTS_INPUT.getCode())
                    .type(DcSystemConfTypeEnum.KEYSTORE.getCode())
                    .confValue(setWhetherHintImportReqVO.getWhetherHint().toString())
                    .updateTime(new Date())
                    .build();
            dcSystemConfService.insertSelective(dcSystemConf);
        } else {
            dcSystemConf = DcSystemConf.builder()
                    .confId(dcSystemConf.getConfId())
                    .confValue(setWhetherHintImportReqVO.getWhetherHint().toString())
                    .updateTime(new Date())
                    .build();
            dcSystemConfService.updateByPrimaryKey(dcSystemConf);
        }

        return ResultInfoUtil.successResult();
    }

    @ApiOperation(value = "Get whether to ask for entering the keystore password")
    @PostMapping("getWhetherHintImport")
    public ResultInfo getWhetherHintImport() {
        DcSystemConf dcSystemConf = dcSystemConfService.querySystemConfByCode(SystemConfCodeEnum.NO_MORE_PROMPTS_INPUT.getCode());
        return ResultInfoUtil.successResult(dcSystemConf);
    }

    @PostMapping("deleteWallet")
    public ResultInfo deleteWallet() {
        boolean deleteWallet = walletService.deleteWallet();
        return ResultInfoUtil.successResult(deleteWallet);
    }

}
