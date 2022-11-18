package com.spartan.dc.service.impl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reddate.spartan.net.SpartanGovern;
import com.spartan.dc.core.dto.dc.DataCenter;
import com.spartan.dc.core.exception.GlobalException;
import com.spartan.dc.core.util.common.CacheManager;
import com.spartan.dc.core.util.common.FileUtil;
import com.spartan.dc.dao.write.SysDataCenterMapper;
import com.spartan.dc.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.crypto.*;
import org.web3j.utils.Strings;

import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.List;
import java.util.Objects;

import static com.spartan.dc.core.util.common.CacheManager.PASSWORD_CACHE_KEY;

/**
 * @author wxq
 * @create 2022/8/18 18:58
 * @description wallet service
 */
@Service
public class WalletServiceImpl implements WalletService {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Value("${chain.walletFilePath}")
    public String walletFilePath;

    static {
        OBJECT_MAPPER.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Autowired
    private SysDataCenterMapper sysDataCenterMapper;

    @Override
    public String generateNewWalletFile(String password) throws InvalidAlgorithmParameterException, CipherException, NoSuchAlgorithmException, IOException, NoSuchProviderException {
        if (Strings.isEmpty(walletFilePath)) {
            throw new GlobalException("Wallet file path is not configured");
        }
        FileUtil.createOrExistsDir(new File(walletFilePath));
        return WalletUtils.generateLightNewWalletFile(password, new File(walletFilePath));
    }

    @Override
    public String generateNewWalletFile(String password, String privateKey) throws CipherException, IOException {

        if (Strings.isEmpty(walletFilePath)) {
            throw new GlobalException("Wallet file path is not configured");
        }

        // private key
        Credentials credentials = Credentials.create(privateKey);
        String address = credentials.getAddress();

        // database ntt account info
        DataCenter dataCenter = sysDataCenterMapper.getDataCenter();
        if (dataCenter != null) {
            if (!dataCenter.getNttAccountAddress().equalsIgnoreCase(address)) {
                throw new GlobalException("NTT wallet and private key do not match");
            }
        }

        String walletFile = walletFilePath + "/" + address + ".json";
        if (Objects.nonNull(FileUtil.getFileByPath(walletFile))) {
            FileUtil.deleteFile(walletFile);
        }
        CacheManager.put(PASSWORD_CACHE_KEY, password);
        return generateWalletFile(password, credentials, new File(walletFilePath));
    }

    @Override
    public Credentials loadWallet(String password, String walletFilePath) throws CipherException, IOException {
        Credentials credentials = WalletUtils.loadCredentials(password, walletFilePath);
        return credentials;
    }

    @Override
    public Credentials loadWallet(String password) throws CipherException, IOException {
        List<String> walletFileName = FileUtil.queryFileNamesWithoutSuffix(walletFilePath);
        if (walletFileName.size() == 0) {
            throw new GlobalException("The keystore information is not configured");
        }
        Credentials credentials = WalletUtils.loadCredentials(password, walletFilePath + "/" + walletFileName.get(0) + ".json");
        return credentials;
    }

    @Override
    public boolean checkWalletExists() {
        List<String> walletFileName = FileUtil.queryFileNamesWithoutSuffix(walletFilePath);
        return walletFileName.size() > 0;
    }

    private static String generateWalletFile(
            String password, Credentials credentials, File destinationDirectory)
            throws CipherException, IOException {

        WalletFile walletFile = Wallet.createStandard(password, credentials.getEcKeyPair());

        String fileName = credentials.getAddress();
        fileName = fileName + ".json";

        File destination = new File(destinationDirectory, fileName);
        OBJECT_MAPPER.writeValue(destination, walletFile);

        SpartanGovern.setNonceManagerAddress(credentials.getAddress());

        return fileName;
    }
}
