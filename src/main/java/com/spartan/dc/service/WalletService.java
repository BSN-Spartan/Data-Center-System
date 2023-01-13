package com.spartan.dc.service;

import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;

import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

/**
 * @author wxq
 * @create 2022/8/18 18:57
 * @description wallet service
 */
public interface WalletService {
    /**
     * Generate New Wallet File
     *
     * @param password
     * @return
     * @throws InvalidAlgorithmParameterException
     * @throws CipherException
     * @throws NoSuchAlgorithmException
     * @throws IOException
     * @throws NoSuchProviderException
     */
    String generateNewWalletFile(String password) throws InvalidAlgorithmParameterException, CipherException, NoSuchAlgorithmException, IOException, NoSuchProviderException;


    /**
     * Generate New Wallet File
     *
     * @param privateKey
     * @param address
     * @return
     * @throws InvalidAlgorithmParameterException
     * @throws CipherException
     * @throws NoSuchAlgorithmException
     * @throws IOException
     * @throws NoSuchProviderException
     */
    String generateNewWalletFile(String privateKey, String address) throws InvalidAlgorithmParameterException, CipherException, NoSuchAlgorithmException, IOException, NoSuchProviderException;


    /**
     * Load Wallet By password
     *
     * @param password
     * @param walletFilePath
     * @return
     */
    Credentials loadWallet(String password, String walletFilePath) throws CipherException, IOException;

    /**
     * Load Wallet By password
     *
     * @param password
     * @return
     */
    Credentials loadWallet(String password) throws CipherException, IOException;


    /**
     * Check Wallet Exists
     *
     * @return
     */
    boolean checkWalletExists();

    /**
     * Delete Wallet
     *
     * @return
     */
    boolean deleteWallet();

}
