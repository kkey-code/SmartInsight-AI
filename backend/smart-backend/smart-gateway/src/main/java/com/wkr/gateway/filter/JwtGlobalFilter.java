package com.wkr.gateway.filter;

import com.wkr.core.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
public class JwtGlobalFilter implements GlobalFilter, Ordered {

    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String USERNAME_HEADER = "X-Username";
    private static final String USER_ROLES_HEADER = "X-User-Roles";

    private static final Set<String> WHITELIST = Set.of(
            "/auth/login",
            "/auth/register",
            "/actuator/health"
    );

    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange,
            GatewayFilterChain chain
    ) {

        String path = exchange.getRequest()
                .getURI()
                .getPath();

        // 部分接口放行
        if (WHITELIST.contains(path)) {
            return chain.filter(exchange);
        }

        // 获取 JWT 令牌
        String authorization = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        if (authorization == null
                || !authorization.startsWith("Bearer ")) {
            return unauthorized(exchange);
        }
        // 解析 JWT
        try {
            String token = authorization.substring(7);

            Claims claims = JwtUtil.parse(token);

            Object userId = claims.get("userId");
            String username = claims.getSubject();

            List<String> roles = claims.get("roles", List.class);

            if (roles == null) {
                roles = Collections.emptyList();
            }
            if (userId == null || username == null) {
                return unauthorized(exchange);
            }

            // 保存到 Gateway 当前请求上下文
            exchange.getAttributes().put("userId", userId);
            exchange.getAttributes().put("username", username);
            exchange.getAttributes().put("roles", roles);

            /*
             * 防止客户端伪造身份 Header。
             *
             * 先删除客户端传入的身份信息，
             * 再由 Gateway 根据 JWT 重新写入。
             */
            List<String> finalRoles = roles;
            ServerWebExchange mutatedExchange =
                exchange.mutate()
                    .request(request ->
                            request.headers(headers -> {

                        // 1. 先删除客户端传来的身份信息（防止伪造）
                        headers.remove(USER_ID_HEADER);
                        headers.remove(USERNAME_HEADER);
                        headers.remove(USER_ROLES_HEADER);
                        // 2. 重新写入从 JWT 解析出的身份信息
                        headers.add(
                                USER_ID_HEADER,
                                String.valueOf(userId)
                        );

                        headers.add(
                                USERNAME_HEADER,
                                username
                        );

                        if (!finalRoles.isEmpty()) {
                            headers.add(
                                    USER_ROLES_HEADER,
                                    String.join(",", finalRoles)
                            );
                        }
                    }))
                    .build();

            return chain.filter(mutatedExchange);

        } catch (Exception e) {
            log.warn("JWT validation failed", e);
            return unauthorized(exchange);
        }
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {

        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse()
                .getHeaders()
                .setContentType(MediaType.APPLICATION_JSON);

        String body = """
            {
                "code": 401,
                "message": "未登录或登录已过期",
                "data": null
            }
            """;

        DataBuffer buffer = exchange.getResponse()
                .bufferFactory()
                .wrap(body.getBytes(StandardCharsets.UTF_8));

        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return -100;
    }
}