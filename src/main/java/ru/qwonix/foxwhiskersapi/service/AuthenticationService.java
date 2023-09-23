package ru.qwonix.foxwhiskersapi.service;

import org.springframework.security.core.GrantedAuthority;
import ru.qwonix.foxwhiskersapi.exception.TokenValidationException;
import ru.qwonix.foxwhiskersapi.security.Token;

import java.time.Duration;
import java.util.Collection;

public interface AuthenticationService {


    void setAuthenticationCodeTtl(Duration authenticationCodeTtl);

    void setAccessTokenTtl(Duration accessTokenTtl);

    void setRefreshTokenTtl(Duration refreshTokenTtl);

    boolean verifyAuthenticationCode(String username, String code);

    String createAuthenticationCode(String username);

    Boolean clearAuthenticationCode(String username);

    String serializeAccessToken(Token token);

    String serializeRefreshToken(Token token);

    Token parseAccessToken(String token) throws TokenValidationException;

    Token parseRefreshToken(String token) throws TokenValidationException;
}
