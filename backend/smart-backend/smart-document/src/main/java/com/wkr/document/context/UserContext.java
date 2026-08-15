package com.wkr.document.context;

import java.util.List;

public final class UserContext {
    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();
    private static final ThreadLocal<String> USERNAME = new ThreadLocal<>();
    private static final ThreadLocal<List<String>> ROLES = new ThreadLocal<>();

    private UserContext() {
    }

    public static void set(Long userId, String username, List<String> roles) {
        USER_ID.set(userId);
        USERNAME.set(username);
        ROLES.set(roles);
    }

    public static Long getUserId() {
        return USER_ID.get();
    }

    public static boolean isAdmin() {
        List<String> roles = ROLES.get();
        return roles != null && roles.contains("ADMIN");
    }

    public static void clear() {
        USER_ID.remove();
        USERNAME.remove();
        ROLES.remove();
    }
}
