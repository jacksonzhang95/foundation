package com.foundation.common.utils;

import java.security.MessageDigest;

/**
 * @author : jacksonz
 * @date : 2022/7/9 14:33
 * @description :
 */
public class MD5Utils {

    public static String MD5(String data) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] array = md.digest(data.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte item : array) {
                sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
            }

            return sb.toString().toUpperCase();
        } catch (Exception exception) {
        }
        return null;

    }
}
