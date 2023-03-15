package com.spartan.dc.core.util.common;


import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;


public class AesUtil {
    private AesUtil() {
        throw new IllegalStateException("Utility class");
    }

    private static final String KEY_ALGORITHM = "AES";
    private static final String DEFAULT_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";

    public static String decrypt(String encryptStr, String secretKey) throws Exception {

        Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);

        cipher.init(Cipher.DECRYPT_MODE, getSecretKey(secretKey));

        byte[] result = cipher.doFinal(Base64.decodeBase64(encryptStr));
        return new String(result, "utf-8");
    }

    public static void main(String[] args) throws Exception {
        String encrypt = "qVQNXJ9rqlsXtctxphQBELbGqtnhwfmXMitXWULMmSQ=";
        String key = "SPRING_MAIL_KEY";
        String decrypt = decrypt(encrypt, key);
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
}
