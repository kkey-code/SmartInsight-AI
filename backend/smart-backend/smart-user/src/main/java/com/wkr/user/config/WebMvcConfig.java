package com.wkr.user.config;

import com.wkr.user.interceptor.InternalAuthInterceptor;
import com.wkr.user.interceptor.RoleInterceptor;
import com.wkr.web.interceptor.UserContextInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final UserContextInterceptor userContextInterceptor;
    private final RoleInterceptor roleInterceptor;
    private final InternalAuthInterceptor internalAuthInterceptor;

    public WebMvcConfig(
            UserContextInterceptor userContextInterceptor,
            RoleInterceptor roleInterceptor,
            InternalAuthInterceptor internalAuthInterceptor
    ) {
        this.userContextInterceptor = userContextInterceptor;
        this.roleInterceptor = roleInterceptor;
        this.internalAuthInterceptor = internalAuthInterceptor;
    }

    @Override
    public void addInterceptors(
            InterceptorRegistry registry
    ) {

        registry.addInterceptor(internalAuthInterceptor)
                .addPathPatterns("/inner/**");

        registry.addInterceptor(userContextInterceptor)
                .addPathPatterns("/user/**");

        registry.addInterceptor(roleInterceptor)
                .addPathPatterns("/user/**");
    }
}