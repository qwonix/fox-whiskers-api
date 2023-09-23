package ru.qwonix.foxwhiskersapi.security.converter;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationConverter;
import ru.qwonix.foxwhiskersapi.security.JwtAuthenticationToken;

@Slf4j
public class JwtAuthenticationConverter implements AuthenticationConverter {

    private static final String AUTHENTICATION_SCHEME = "Bearer";

    @Override
    public Authentication convert(HttpServletRequest request) {
        log.info("token authentication request");
        final var token = obtainToken(request);
        if (token != null) {
            return new JwtAuthenticationToken(token);
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
