package com.newsline.common;

public final class Utils {
    private Utils(){};
    public static boolean isNullOrBlank(String str) {
        return str == null || str.isBlank();
    }
}
