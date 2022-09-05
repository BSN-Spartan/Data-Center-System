package com.spartan.dc.core.util.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * Md5
 */
public class Md5Utils {
    private static final Logger log = LoggerFactory.getLogger(Md5Utils.class);

    private static byte[] md5(String s) {
        MessageDigest algorithm;
        try {
            algorithm = MessageDigest.getInstance("MD5");
            algorithm.reset();
            algorithm.update(s.getBytes("UTF-8"));
            byte[] messageDigest = algorithm.digest();
            return messageDigest;
        } catch (Exception e) {
            log.error("MD5 Error...", e);
        }
        return null;
    }


    public static String getMD5String(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            return new BigInteger(1, md.digest()).toString(16);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static final String toHex(byte[] hash) {
        if (hash == null) {
            return null;
        }
        StringBuffer buf = new StringBuffer(hash.length * 2);
        int i;

        for (i = 0; i < hash.length; i++) {
            if ((hash[i] & 0xff) < 0x10) {
                buf.append("0");
            }
            buf.append(Long.toString(hash[i] & 0xff, 16));
        }
        return buf.toString();
    }

    public static String hash(String s) {
        try {
            return new String(toHex(md5(s)).getBytes("UTF-8"), "UTF-8");
        } catch (Exception e) {
            log.error("not supported charset...{}", e);
            return s;
        }
    }


    public static String encodeUtf8(String reqData) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.reset();
        md.update(reqData.getBytes("UTF-8"));
        byte[] md5 = md.digest();
        String encodeData = byteArrayToHex(md5);
        return encodeData.toLowerCase();
    }

    private static String byteArrayToHex(byte[] bytes) {
        String retorno = "";
        if (bytes == null || bytes.length == 0) {
            return retorno;
        }
        for (int i = 0; i < bytes.length; i++) {
            byte valor = bytes[i];
            int d1 = valor & 0xF;
            d1 += (d1 < 10) ? 48 : 55;
            int d2 = (valor & 0xF0) >> 4;
            d2 += (d2 < 10) ? 48 : 55;
            retorno = retorno + (char) d2 + (char) d1;
        }
        return retorno;
    }
}
