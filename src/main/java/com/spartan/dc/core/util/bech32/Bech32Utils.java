package com.spartan.dc.core.util.bech32;

import org.bouncycastle.util.encoders.Hex;

import java.io.ByteArrayOutputStream;

/**
 * @Author : rjx
 * @Date : 2022/9/23 9:47
 **/
public class Bech32Utils {

    public static boolean validAddress(String address) {
        fromBech32(address);
        return true;
    }

    public static String toBech32(String hrp, byte[] pubkeyHex) {
        byte[] bits = convertBits(pubkeyHex, 0, pubkeyHex.length, 8, 5, true);
        return Bech32.encode(Bech32.Encoding.BECH32, hrp, bits);
    }

    public static byte[] fromBech32(String address) {
        Bech32.Bech32Data data = Bech32.decode(address);
        byte[] bits = convertBits(data.data, 0, data.data.length, 5, 8, true);
        return bits;
    }

    public static String bech32ToHex(String address) {
        return Hex.toHexString(fromBech32(address)).toUpperCase();
    }

    public static String hexToBech32(String hrp, String hexAddress) {
        return toBech32(hrp, Hex.decode(hexAddress));
    }


    public static byte[] convertBits(final byte[] in, final int inStart, final int inLen, final int fromBits,
                                     final int toBits, final boolean pad) throws AddressFormatException {
        int acc = 0;
        int bits = 0;
        ByteArrayOutputStream out = new ByteArrayOutputStream(64);
        final int maxv = (1 << toBits) - 1;
        final int max_acc = (1 << (fromBits + toBits - 1)) - 1;
        for (int i = 0; i < inLen; i++) {
            int value = in[i + inStart] & 0xff;
            if ((value >>> fromBits) != 0) {
                throw new AddressFormatException(
                        String.format("Input value '%X' exceeds '%d' bit size", value, fromBits));
            }
            acc = ((acc << fromBits) | value) & max_acc;
            bits += fromBits;
            while (bits >= toBits) {
                bits -= toBits;
                out.write((acc >>> bits) & maxv);
            }
        }
        if (pad) {
            if (bits > 0)
                out.write((acc << (toBits - bits)) & maxv);
        } else if (bits >= fromBits || ((acc << (toBits - bits)) & maxv) != 0) {
            throw new AddressFormatException("Could not convert bits, invalid padding");
        }
        return out.toByteArray();
    }

    public static void main(String[] args) {
        String hexAddress = "e9408E6563BdA549Db5182cD2E5F0334feee5009";
        String iaaAddress = hexToBech32("iaa", hexAddress);

        String convertAddress = bech32ToHex(iaaAddress);

        System.out.println("hex === " + hexAddress);
        System.out.println("iaa === " + iaaAddress);
        System.out.println("convert hex === " + convertAddress);

    }

}
