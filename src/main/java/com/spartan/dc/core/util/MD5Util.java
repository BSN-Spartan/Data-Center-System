package com.spartan.dc.core.util;

import java.security.MessageDigest;


public class MD5Util {

    public static String encode(String reqData) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.reset();
        md.update(reqData.getBytes("GBK"));
        byte[] md5 = md.digest();
        String encodeData = byteArrayToHex(md5);
        return encodeData;
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
