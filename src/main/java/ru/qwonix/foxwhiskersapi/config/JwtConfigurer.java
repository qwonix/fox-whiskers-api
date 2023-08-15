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
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import ru.qwonix.foxwhiskersapi.security.TokenAuthenticationUserDetailsService;
import ru.qwonix.foxwhiskersapi.security.converter.CodeVerificationAuthenticationConverter;
import ru.qwonix.foxwhiskersapi.security.converter.JwtAuthenticationRequestConverter;
import ru.qwonix.foxwhiskersapi.security.converter.JwtRefreshConverter;
import ru.qwonix.foxwhiskersapi.security.filter.RequestTokensFilter;
import ru.qwonix.foxwhiskersapi.service.AuthenticationService;
import ru.qwonix.foxwhiskersapi.service.UserService;

@RequiredArgsConstructor
public class JwtConfigurer extends AbstractHttpConfigurer<JwtConfigurer, HttpSecurity> {

    private final AuthenticationService authenticationService;
    private final UserService userService;
    private RequestMatcher authenticationRequestMatcher = new AntPathRequestMatcher("/api/v1/auth", HttpMethod.POST.name());
    private RequestMatcher refreshRequestMatcher = new AntPathRequestMatcher("/api/v1/auth/refresh", HttpMethod.POST.name());

    @Override
    public void init(HttpSecurity builder) {
        var csrfConfigurer = builder.getConfigurer(CsrfConfigurer.class);
        if (csrfConfigurer != null) {
            csrfConfigurer.ignoringRequestMatchers(authenticationRequestMatcher);
        }
    }

    @Override
    public void configure(HttpSecurity builder) {
        final var authenticationManager =
                builder.getSharedObject(AuthenticationManager.class);

        var jwtAuthenticationFilter = new AuthenticationFilter(
                authenticationManager,
                new JwtAuthenticationRequestConverter(authenticationService)
        );
        // all requests except those related to authentication
        jwtAuthenticationFilter.setRequestMatcher(new NegatedRequestMatcher(new OrRequestMatcher(authenticationRequestMatcher, refreshRequestMatcher)));
        jwtAuthenticationFilter.setSuccessHandler((request, response, authentication) -> CsrfFilter.skipRequest(request));
        jwtAuthenticationFilter.setFailureHandler((request, response, exception) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED));
        jwtAuthenticationFilter.setBeanName("JWT Authentication Filter");

        var jwtRefreshFilter = new AuthenticationFilter(
                authenticationManager,
                new JwtRefreshConverter(authenticationService)
        );
        jwtRefreshFilter.setRequestMatcher(refreshRequestMatcher);
        jwtRefreshFilter.setSuccessHandler((request, response, authentication) -> CsrfFilter.skipRequest(request));
        jwtRefreshFilter.setFailureHandler((request, response, exception) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED));
        jwtRefreshFilter.setBeanName("JWT Refresh Filter");

        var codeVerificationAuthenticationFilter = new AuthenticationFilter(
                authenticationManager,
                new CodeVerificationAuthenticationConverter(authenticationService)
        );
        codeVerificationAuthenticationFilter.setRequestMatcher(authenticationRequestMatcher);
        codeVerificationAuthenticationFilter.setSuccessHandler((request, response, authentication) -> CsrfFilter.skipRequest(request));
        codeVerificationAuthenticationFilter.setFailureHandler((request, response, exception) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED));
        codeVerificationAuthenticationFilter.setBeanName("Сode Authentication Verification Filter");

        var authenticationProvider = new PreAuthenticatedAuthenticationProvider();
        var codeVerificationAuthenticationUserDetailsService = new TokenAuthenticationUserDetailsService(userService, authenticationService);
        authenticationProvider.setPreAuthenticatedUserDetailsService(codeVerificationAuthenticationUserDetailsService);

        var requestTokensFilter = new RequestTokensFilter(authenticationService);
        requestTokensFilter.setRequestMatcher(new OrRequestMatcher(authenticationRequestMatcher, refreshRequestMatcher));

        builder
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtRefreshFilter, CsrfFilter.class)
                .addFilterAfter(jwtAuthenticationFilter, jwtRefreshFilter.getClass())
                .addFilterAfter(requestTokensFilter, ExceptionTranslationFilter.class)
                .addFilterBefore(codeVerificationAuthenticationFilter, RequestTokensFilter.class)
        ;
    }

    public JwtConfigurer authenticationRequestMatcher(RequestMatcher requestMatcher) {
        this.authenticationRequestMatcher = requestMatcher;
        return this;
    }

    public JwtConfigurer refreshRequestMatcher(RequestMatcher requestMatcher) {
        this.refreshRequestMatcher = requestMatcher;
        return this;
    }
}
