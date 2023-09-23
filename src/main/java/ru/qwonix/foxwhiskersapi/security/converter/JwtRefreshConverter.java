package ru.qwonix.foxwhiskersapi.security.converter;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import ru.qwonix.foxwhiskersapi.entity.Permission;
import ru.qwonix.foxwhiskersapi.exception.TokenValidationException;
import ru.qwonix.foxwhiskersapi.security.Token;
import ru.qwonix.foxwhiskersapi.service.AuthenticationService;

@Slf4j
@RequiredArgsConstructor
public class JwtRefreshConverter implements AuthenticationConverter {

    private static final String AUTHENTICATION_SCHEME = "Bearer";

    private final AuthenticationService authenticationService;

    @Override
    public Authentication convert(HttpServletRequest request) {
        log.info("token refresh request");
        final var token = obtainToken(request);
        if (token != null) {
            try {
                Token refreshToken = authenticationService.parseRefreshToken(token);
                if (refreshToken != null && refreshToken.authorities().contains(Permission.TOKEN_REFRESH)) {
                    return new PreAuthenticatedAuthenticationToken(refreshToken.subject(), token, refreshToken.authorities());
                }
            } catch (TokenValidationException e) {
                return new PreAuthenticatedAuthenticationToken(token, token);
            }
        }
        return null;
    }


    /**
     * @param request so that request attributes can be retrieved
     * @return the token that will be presented in the Authentication request token to the AuthenticationManager
     */
    private String obtainToken(HttpServletRequest request) {
        final var authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorization != null && authorization.startsWith(AUTHENTICATION_SCHEME)) {
            return authorization.substring(AUTHENTICATION_SCHEME.length() + 1);
        }
        return null;
    }
}
