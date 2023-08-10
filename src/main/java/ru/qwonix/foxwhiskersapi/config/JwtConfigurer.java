package ru.qwonix.foxwhiskersapi.config;

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
import org.springframework.security.web.util.matcher.RequestMatcher;
import ru.qwonix.foxwhiskersapi.security.CodeVerificationAuthenticationUserDetailsService;
import ru.qwonix.foxwhiskersapi.security.filter.CodeVerificationAuthenticationConverter;
import ru.qwonix.foxwhiskersapi.security.filter.RequestTokensFilter;
import ru.qwonix.foxwhiskersapi.service.AuthenticationService;

@RequiredArgsConstructor
public class JwtConfigurer extends AbstractHttpConfigurer<JwtConfigurer, HttpSecurity> {

    private RequestMatcher requestMatcher = new AntPathRequestMatcher("/api/v1/auth", HttpMethod.POST.name());
    private final AuthenticationService authenticationService;

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
        var codeAuthenticationFilter = new AuthenticationFilter(authenticationManager,
                new CodeVerificationAuthenticationConverter(authenticationService));
        codeAuthenticationFilter.setSuccessHandler((request, response, authentication) -> CsrfFilter.skipRequest(request));
        codeAuthenticationFilter.setRequestMatcher(requestMatcher);

        var authenticationProvider = new PreAuthenticatedAuthenticationProvider();
        authenticationProvider.setPreAuthenticatedUserDetailsService(new CodeVerificationAuthenticationUserDetailsService());
        builder.addFilterAfter(new RequestTokensFilter(authenticationService), ExceptionTranslationFilter.class)
                .addFilterBefore(codeAuthenticationFilter, RequestTokensFilter.class)
                .authenticationProvider(authenticationProvider);
    }

    public JwtConfigurer requestMatcher(RequestMatcher requestMatcher) {
        this.requestMatcher = requestMatcher;
        return this;
    }
}
