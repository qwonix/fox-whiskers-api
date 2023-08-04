package ru.qwonix.foxwhiskersapi.config;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationEntryPointFailureHandler;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import ru.qwonix.foxwhiskersapi.security.JwtAuthenticationConverter;
import ru.qwonix.foxwhiskersapi.security.JwtAuthenticationProvider;

import java.time.Duration;

public class JwtConfigurer extends AbstractHttpConfigurer<JwtConfigurer, HttpSecurity> {

    private final JwtAuthenticationProvider authenticationProvider;

    public JwtConfigurer(JwtAuthenticationProvider authenticationProvider) {
        this.authenticationProvider = authenticationProvider;
    }

    public JwtConfigurer setAccessTtl(Duration accessTtl) {
        this.authenticationProvider.setAccessTtl(accessTtl);
        return this;
    }

    public JwtConfigurer setRefreshTtl(Duration refreshTtl) {
        this.authenticationProvider.setRefreshTtl(refreshTtl);
        return this;
    }

    private AuthenticationEntryPoint authenticationEntryPoint = (request, response, authException) -> {
        response.addHeader(HttpHeaders.WWW_AUTHENTICATE, "jwt");
        response.sendError(HttpStatus.UNAUTHORIZED.value());
    };

    public JwtConfigurer setAuthenticationEntryPoint(AuthenticationEntryPoint authenticationEntryPoint) {
        this.authenticationEntryPoint = authenticationEntryPoint;
        return this;
    }

    @Override
    public void init(HttpSecurity builder) throws Exception {
        builder.exceptionHandling(exceptionHandling -> exceptionHandling.authenticationEntryPoint(this.authenticationEntryPoint));
    }

    @Override
    public void configure(HttpSecurity builder) {
        final var authenticationManager =
                builder.getSharedObject(AuthenticationManager.class);

        final var authenticationFilter = new AuthenticationFilter(authenticationManager, new JwtAuthenticationConverter());
        authenticationFilter.setSuccessHandler((request, response, authentication) -> { });
        authenticationFilter.setFailureHandler((request, response, exception) -> new AuthenticationEntryPointFailureHandler(this.authenticationEntryPoint));

        builder.addFilterBefore(authenticationFilter, BasicAuthenticationFilter.class)
                .authenticationProvider(authenticationProvider);
    }


}
