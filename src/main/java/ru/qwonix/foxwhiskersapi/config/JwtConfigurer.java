package ru.qwonix.foxwhiskersapi.config;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import ru.qwonix.foxwhiskersapi.security.CodeVerificationAuthenticationUserDetailsService;
import ru.qwonix.foxwhiskersapi.security.converter.CodeVerificationAuthenticationConverter;
import ru.qwonix.foxwhiskersapi.security.converter.JwtAuthenticationRequestConverter;
import ru.qwonix.foxwhiskersapi.security.filter.RequestTokensFilter;
import ru.qwonix.foxwhiskersapi.service.AuthenticationService;

@RequiredArgsConstructor
public class JwtConfigurer extends AbstractHttpConfigurer<JwtConfigurer, HttpSecurity> {

    private final AuthenticationService authenticationService;
    private RequestMatcher requestMatcher = new AntPathRequestMatcher("/api/v1/auth", HttpMethod.POST.name());

    @Override
    public void init(HttpSecurity builder) throws Exception {
        var csrfConfigurer = builder.getConfigurer(CsrfConfigurer.class);
        if (csrfConfigurer != null) {
            csrfConfigurer.ignoringRequestMatchers(requestMatcher);
        }
    }

    @Override
    public void configure(HttpSecurity builder) throws Exception {
        final var authenticationManager =
                builder.getSharedObject(AuthenticationManager.class);

        var jwtAuthenticationFilter = new AuthenticationFilter(
                authenticationManager,
                new JwtAuthenticationRequestConverter(authenticationService)
        );
        jwtAuthenticationFilter.setSuccessHandler((request, response, authentication) -> CsrfFilter.skipRequest(request));
        jwtAuthenticationFilter.setFailureHandler((request, response, exception) -> response.sendError(HttpServletResponse.SC_FORBIDDEN));
        jwtAuthenticationFilter.setRequestMatcher(new NegatedRequestMatcher(requestMatcher));
        jwtAuthenticationFilter.setBeanName("jwtAuthenticationFilter");

        var codeVerificationAuthenticationFilter = new AuthenticationFilter(
                authenticationManager,
                new CodeVerificationAuthenticationConverter(authenticationService)
        );
        codeVerificationAuthenticationFilter.setRequestMatcher(requestMatcher);
        codeVerificationAuthenticationFilter.setSuccessHandler((request, response, authentication) -> CsrfFilter.skipRequest(request));
        codeVerificationAuthenticationFilter.setFailureHandler((request, response, exception) -> response.sendError(HttpServletResponse.SC_FORBIDDEN));
        codeVerificationAuthenticationFilter.setBeanName("codeVerificationAuthenticationFilter");

        var authenticationProvider = new PreAuthenticatedAuthenticationProvider();
        authenticationProvider.setPreAuthenticatedUserDetailsService(new CodeVerificationAuthenticationUserDetailsService());

        var requestTokensFilter = new RequestTokensFilter(authenticationService);
        requestTokensFilter.setRequestMatcher(requestMatcher);

        builder
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, CsrfFilter.class)
                .addFilterAfter(requestTokensFilter, ExceptionTranslationFilter.class)
                .addFilterBefore(codeVerificationAuthenticationFilter, RequestTokensFilter.class)
                ;
    }

    public JwtConfigurer requestMatcher(RequestMatcher requestMatcher) {
        this.requestMatcher = requestMatcher;
        return this;
    }
}
