package com.wkr.user.interceptor;

import com.wkr.user.annotation.RequireRole;
import com.wkr.web.context.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;

@Component
public class RoleInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler
    ) {

        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        RequireRole annotation =
                handlerMethod.getMethodAnnotation(RequireRole.class);

        if (annotation == null) {
            annotation =
                    handlerMethod.getBeanType()
                            .getAnnotation(RequireRole.class);
        }

        // 没有权限要求，直接放行
        if (annotation == null) {
            return true;
        }

        boolean hasRole = Arrays.stream(annotation.value())
                .anyMatch(UserContext::hasRole);

        if (!hasRole) {
            response.setStatus(
                    HttpServletResponse.SC_FORBIDDEN
            );
            return false;
        }

        return true;
    }
}