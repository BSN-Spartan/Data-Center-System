package com.spartan.dc.core.util.common;


import org.web3j.utils.Numeric;

import java.util.regex.Pattern;

/**
 * Desc：
 *
 * @Created by 2022-07-21 10:27
 */
public class CheckUtil {



    public static boolean checkEmail(String email) {
        return Pattern.matches("^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$", email);
    }

    public static boolean checkNttAddress(String nttAccountAddress) {
        if (nttAccountAddress == null || nttAccountAddress.length() == 0 || !nttAccountAddress.startsWith("0x")) {
            return false;
        }
        return isValidAddress(nttAccountAddress);
    }


    private static boolean isValidAddress(String input) {
        String cleanInput = Numeric.cleanHexPrefix(input);
        try {
            Numeric.toBigIntNoPrefix(cleanInput);
        } catch (NumberFormatException e) {
            return false;
        }
        return cleanInput.length() == 40;
    }
}
