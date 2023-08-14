package ru.qwonix.foxwhiskersapi.security.converter;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import ru.qwonix.foxwhiskersapi.exception.InvalidTokenFormatException;
import ru.qwonix.foxwhiskersapi.service.AuthenticationService;

import java.util.Base64;

@Slf4j
@RequiredArgsConstructor
public class CodeVerificationAuthenticationConverter implements AuthenticationConverter {

    public static final char TOKEN_SEPARATOR = ':';
    private static final String AUTHENTICATION_SCHEME = "CodeVerification";

    private final AuthenticationService authenticationService;

    @Override
    public Authentication convert(HttpServletRequest request) {
        log.info("Code authentication request");
        try {
            String token = obtainToken(request);
            if (token != null) {
                final var username = token.split(":")[0];
                final var code = token.split(":")[1];

                if (authenticationService.verifyAuthenticationCode(username, code)) {
                    return new PreAuthenticatedAuthenticationToken(username, code);
                } else {
                    throw new BadCredentialsException("Verification code is incorrect");
                }
            }
        } catch (IllegalArgumentException e) {
            throw new InvalidTokenFormatException("Token must be encoded in base64: base64(username + ':' + code)");
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new InvalidTokenFormatException("Data in the token must be separated by colon: base64(username + ':' + code)");
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
            final var encodedVerificationCode = authorization.substring(AUTHENTICATION_SCHEME.length() + 1);
            return new String(Base64.getDecoder().decode(encodedVerificationCode));
        }
        return null;
    }
}
