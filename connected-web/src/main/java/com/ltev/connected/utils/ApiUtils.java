package com.ltev.connected.utils;

public class ApiUtils {

    public static boolean isNumber(String str) {
        try {
            long l = Long.parseLong(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}