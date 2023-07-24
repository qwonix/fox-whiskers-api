package ru.qwonix.foxwhiskersapi.security;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;

import java.io.IOException;

@Slf4j
public class JwtAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private final RequestMatcher excludeRequestMatcher;

    public JwtAuthenticationFilter(String defaultFilterProcessesUrl, AuthenticationManager authenticationManager, RequestMatcher excludeRequestMatcher) {
        super(defaultFilterProcessesUrl, authenticationManager);
        super.setAuthenticationSuccessHandler((request1, response1, authentication) -> {
        });
        Assert.notNull(excludeRequestMatcher, "requestMatcher cannot be null");
        this.excludeRequestMatcher = excludeRequestMatcher;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        final String token = obtainToken(request);
        log.info("attempt authentication: {}", token);

        JwtAuthenticationToken authRequest = JwtAuthenticationToken.unauthenticated(token);

        return this.getAuthenticationManager().authenticate(authRequest);
    }

    @Override
    protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
        return super.requiresAuthentication(request, response) && !excludeRequestMatcher.matches(request);
    }

    /**
     * @param request so that request attributes can be retrieved
     * @return the token that will be presented in the Authentication request token to the AuthenticationManager
     */
    private String obtainToken(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        super.successfulAuthentication(request, response, chain, authResult);
        log.info("successful authentication: {}", authResult.getPrincipal());
        chain.doFilter(request, response);
    }
}