package ru.qwonix.foxwhiskersapi.security;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.util.Strings;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationConverter;

public class JwtAuthenticationConverter implements AuthenticationConverter {
    @Override
    public Authentication convert(HttpServletRequest request) {
        final var authentication = obtainToken(request);
        if (authentication != null && authentication.startsWith("Barer ")) {
            final var rawToken = authentication.replaceAll("^Barer ", Strings.EMPTY);

            return JwtAuthenticationToken.unauthenticated(rawToken);
        }
        return null;
    }


    /**
     * @param request so that request attributes can be retrieved
     * @return the token that will be presented in the Authentication request token to the AuthenticationManager
     */
    private String obtainToken(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }
}
