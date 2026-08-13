package com.wkr.user.interceptor;

import com.wkr.user.context.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class UserContextInterceptor implements HandlerInterceptor {

    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String USERNAME_HEADER = "X-Username";
    private static final String USER_ROLES_HEADER = "X-User-Roles";

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler
    ) {

        String uri = request.getRequestURI();

        // 内部服务接口暂时不要求用户上下文
        if (uri.startsWith("/inner/")) {
            return true;
        }

        String userId = request.getHeader(USER_ID_HEADER);
        String username = request.getHeader(USERNAME_HEADER);
        String roles = request.getHeader(USER_ROLES_HEADER);

        if (userId == null || username == null) {
            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED
            );
            return false;
        }

        try {
            UserContext.setUserId(Long.valueOf(userId));
            UserContext.setUsername(username);

            List<String> roleList;

            if (roles == null || roles.isBlank()) {
                roleList = Collections.emptyList();
            } else {
                roleList = Arrays.stream(roles.split(","))
                        .map(String::trim)
                        .filter(role -> !role.isBlank())
                        .toList();
            }
            UserContext.setRoles(roleList);
            return true;

        } catch (Exception e) {

            UserContext.clear();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
    }

    @Override
    public void afterCompletion(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler,
            Exception ex
    ) {
        UserContext.clear();
    }
}