package ru.qwonix.foxwhiskersapi.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;

import java.time.Duration;
import java.util.List;

public interface AuthenticationService {

    // FIXME: 11.08.2023 remove constant
    String PERMISSIONS_CLAIM = "PERMISSIONS";

    void setAuthenticationCodeTtl(Duration authenticationCodeTtl);

    void setAccessTokenTtl(Duration accessTokenTtl);

    void setRefreshTokenTtl(Duration refreshTokenTtl);

    boolean verifyCodeAuthentication(String username, String code);

    void sendCode(String phoneNumber);

    String generateAccessToken(String subject, List<String> authorities);

    String generateRefreshToken(String subject);

    Claims getAccessClaims(String token) throws
            ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, io.jsonwebtoken.security.SignatureException, IllegalArgumentException;

    Claims getRefreshClaims(String token) throws
            ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, io.jsonwebtoken.security.SignatureException, IllegalArgumentException;
}
