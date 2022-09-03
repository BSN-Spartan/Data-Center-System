package com.spartan.dc.core.util.common;

import org.bitcoinj.core.Bech32;
import org.bouncycastle.util.encoders.Hex;

public class Bech32Utils {


    public static byte[] fromBech32(String address) {
        Bech32.Bech32Data data = Bech32.decode(address);
        byte[] bits = AddressUtils.convertBits(data.data, 0, data.data.length, 5, 8, true);
        return bits;
    }

    public static String bech32ToHex(String address) {
        return Hex.toHexString(fromBech32(address)).toUpperCase();
    }

    public static void main(String[] args) {
        fromBech32("iaa1pq6peqjxpmuuqvymclsdsj08n87t37hlkx4r0t");
    }
}
