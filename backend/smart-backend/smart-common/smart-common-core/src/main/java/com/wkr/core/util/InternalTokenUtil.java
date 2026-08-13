package com.wkr.core.util;

public final class InternalTokenUtil {

    private static final String INTERNAL_TOKEN =
            "smart-internal-token-2026";

    public static boolean verify(String token) {
        return INTERNAL_TOKEN.equals(token);
    }

    public static String getToken() {
        return INTERNAL_TOKEN;
    }

    private InternalTokenUtil() {
    }
}