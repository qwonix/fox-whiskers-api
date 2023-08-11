package ru.qwonix.foxwhiskersapi.security.converter;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import ru.qwonix.foxwhiskersapi.service.AuthenticationService;

@Slf4j
@RequiredArgsConstructor
public class JwtRefreshConverter implements AuthenticationConverter {

    private final AuthenticationService authenticationService;

    @Override
    public Authentication convert(HttpServletRequest request) {
        log.debug("JwtRefreshConverter convert");
        final var authentication = obtainToken(request);
        if (authentication != null && authentication.startsWith("Barer ")) {
            final var rawToken = authentication.substring(6);

            var refreshClaims = authenticationService.getRefreshClaims(rawToken);
            if (refreshClaims != null) {
                return new PreAuthenticatedAuthenticationToken(refreshClaims.getSubject(), rawToken);
            }
        }
        return null;
    }


    /**
     * @param request so that request attributes can be retrieved
     * @return the token that will be presented in the Authentication request token to the AuthenticationManager
     */
    private String obtainToken(HttpServletRequest request) {
        return request.getHeader(HttpHeaders.AUTHORIZATION);
    }
}
