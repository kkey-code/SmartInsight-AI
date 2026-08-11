package com.wkr.gateway.filter;

import com.wkr.core.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.cloud.gateway.filter.*;
import org.springframework.core.Ordered;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class JwtGlobalFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange,
            GatewayFilterChain chain
    ){
        String path =
                exchange.getRequest()
                        .getURI()
                        .getPath();
        // 登录接口放行
        if(path.startsWith("/auth/login")){
            return chain.filter(exchange);
        }
        String token =
                exchange.getRequest()
                        .getHeaders()
                        .getFirst("Authorization");

        System.out.println("TOKEN=" + token);

        if(token == null ||
                !token.startsWith("Bearer ")){
            return unauthorized(exchange);
        }

        try {
            token = token.substring(7);
            Claims claims = JwtUtil.parse(token);
            exchange.getAttributes()
                    .put(
                            "userId",
                            claims.get("userId")
                    );
        }catch(Exception e){
            return unauthorized(exchange);
        }
        return chain.filter(exchange);
    }

    private Mono<Void> unauthorized(
            ServerWebExchange exchange
    ){
        exchange.getResponse()
                .setStatusCode(
                        HttpStatus.UNAUTHORIZED
                );
        return exchange.getResponse()
                .setComplete();
    }

    @Override
    public int getOrder(){
        return -100;
    }
}