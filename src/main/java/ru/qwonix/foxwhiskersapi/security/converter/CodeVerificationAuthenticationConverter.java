package ru.qwonix.foxwhiskersapi.security.converter;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import ru.qwonix.foxwhiskersapi.service.AuthenticationService;

import java.util.Base64;

@Slf4j
@RequiredArgsConstructor
public class CodeVerificationAuthenticationConverter implements AuthenticationConverter {

    private final AuthenticationService authenticationService;

    @Override
    public Authentication convert(HttpServletRequest request) {
        log.debug("CodeVerificationAuthenticationConverter convert");
        final var authentication = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authentication != null && authentication.startsWith("PhoneVerification ")) {
            final var base64 = authentication.replaceAll("^PhoneVerification ", "");
            final var rawData = new String(Base64.getDecoder().decode(base64));
            final var username = rawData.split(":")[0];
            final var code = rawData.split(":")[1];

            if (authenticationService.verifyCodeAuthentication(username, code)) {
                return new PreAuthenticatedAuthenticationToken(username, code);
            } else {
                throw new BadCredentialsException("Verification code is incorrect");
            }
        }
        return null;
    }
}
