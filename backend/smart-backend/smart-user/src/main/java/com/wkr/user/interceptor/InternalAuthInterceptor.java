package com.wkr.user.interceptor;

import com.wkr.core.util.InternalTokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class InternalAuthInterceptor
        implements HandlerInterceptor {

    private static final String HEADER = "X-Internal-Token";

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler
    ) {

        String uri = request.getRequestURI();

        if (!uri.startsWith("/inner/")) {
            return true;
        }

        String token = request.getHeader(HEADER);

        if (!InternalTokenUtil.verify(token)) {
            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED
            );
            return false;
        }

        return true;
    }
}