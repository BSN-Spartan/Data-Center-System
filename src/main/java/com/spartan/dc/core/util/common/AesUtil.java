package com.spartan.dc.core.util.common;


import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;


public class AesUtil {

    private static final String KEY_ALGORITHM = "AES";
    private static final String DEFAULT_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";


    public static String decrypt(String encryptStr, String secretKey) throws Exception {

        Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);

        cipher.init(Cipher.DECRYPT_MODE, getSecretKey(secretKey));

        byte[] result = cipher.doFinal(Base64.decodeBase64(encryptStr));
        return new String(result, "utf-8");
    }


    public static String encrypt(String sSrc, String secretKey) throws Exception {

        Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
        byte[] byteContent = sSrc.getBytes("utf-8");

        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(secretKey));

        byte[] result = cipher.doFinal(byteContent);

        return Base64.encodeBase64String(result);
    }


    private static SecretKeySpec getSecretKey(final String key) throws NoSuchAlgorithmException {

        KeyGenerator kg = KeyGenerator.getInstance(KEY_ALGORITHM);
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        secureRandom.setSeed(key.getBytes());

        kg.init(128, secureRandom);

        SecretKey secretKey = kg.generateKey();

        return new SecretKeySpec(secretKey.getEncoded(), KEY_ALGORITHM);
    }


    public static void main(String[] args) throws Exception {
//        String sSrc = "11111";
//        String kkk = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ0eXBlIjoiNCIsImVtYWlsIjoic2hpeXVldGFuZ0ByZWRkYXRldGVjaC5jb20iLCJudHRBZGRyZXNzIjoiMHgxMDgzRjY4NDMyNWMxNEYzRDlmNjU4ZjAwNTU5YUFmNzIyMzczNTU1In0.rA6_XJe912jGzBxvJZNHFK334vQjCx-1g8KVfrnAwFg";
//        String encryptStr = encrypt(sSrc,kkk);
//
//        System.out.println(encryptStr);
//        System.out.println(decrypt(encryptStr,kkk));

        String sSrc = "0x94e3e21f6e7221becb289fffc7b1d0aa66a7dbd77f035be6a1e96737990852e8";
        String encryptStr = encrypt(sSrc, "key");

        System.out.println(encryptStr);
        System.out.println(decrypt(encryptStr, "key"));
    }

}
