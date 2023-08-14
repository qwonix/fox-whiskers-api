package ru.qwonix.foxwhiskersapi.service;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.security.core.GrantedAuthority;
import ru.qwonix.foxwhiskersapi.security.Token;

import java.time.Duration;
import java.util.Collection;

public interface AuthenticationService {

    // FIXME: 11.08.2023 remove constant
    String PERMISSIONS_CLAIM = "permissions";

    void setAuthenticationCodeTtl(Duration authenticationCodeTtl);

    void setAccessTokenTtl(Duration accessTokenTtl);

    void setRefreshTokenTtl(Duration refreshTokenTtl);

    boolean verifyAuthenticationCode(String username, String code);

    String createAuthenticationCode(String username);

    Boolean clearAuthenticationCode(String username);

    String generateAccessToken(String subject, Collection<? extends GrantedAuthority> authorities);

    String generateRefreshToken(String subject);

    Token getAccessToken(String token) throws
            ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, SignatureException, IllegalArgumentException;

    Token getRefreshToken(String token) throws
            ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, SignatureException, IllegalArgumentException;
}
