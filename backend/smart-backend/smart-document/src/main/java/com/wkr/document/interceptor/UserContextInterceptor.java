package com.wkr.document.interceptor;

import com.wkr.document.context.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class UserContextInterceptor implements HandlerInterceptor {
    private static final String USER_ID = "X-User-Id";
    private static final String USERNAME = "X-Username";
    private static final String USER_ROLES = "X-User-Roles";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String userId = request.getHeader(USER_ID);
        String username = request.getHeader(USERNAME);

        if (userId == null || username == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        List<String> roles = parseRoles(request.getHeader(USER_ROLES));
        UserContext.set(Long.valueOf(userId), username, roles);
        return true;
    }

    private List<String> parseRoles(String value) {
        if (value == null || value.isBlank()) {
            return Collections.emptyList();
        }
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(role -> !role.isEmpty())
                .toList();
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }
}
