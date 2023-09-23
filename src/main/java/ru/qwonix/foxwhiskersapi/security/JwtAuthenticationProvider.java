package ru.qwonix.foxwhiskersapi.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.util.Assert;
import ru.qwonix.foxwhiskersapi.exception.InvalidTokenFormatException;
import ru.qwonix.foxwhiskersapi.exception.TokenValidationException;
import ru.qwonix.foxwhiskersapi.service.AuthenticationService;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final AuthenticationService authenticationService;

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws InvalidTokenFormatException {
        Assert.isInstanceOf(JwtAuthenticationToken.class, authentication);
        String jwtToken = determineToken(authentication);

        try {
            var token = authenticationService.parseAccessToken(jwtToken);
            var username = token.subject();
            var authorities = token.authorities();

            return JwtAuthenticationToken.authenticated(jwtToken, new User(username, jwtToken, authorities), authorities);
        } catch (TokenValidationException e) {
            return JwtAuthenticationToken.unauthenticated(jwtToken);
        }
    }

    private String determineToken(Authentication authentication) {
        return (authentication.getCredentials() == null) ? "NONE_PROVIDED" : authentication.getCredentials().toString();
    }

}