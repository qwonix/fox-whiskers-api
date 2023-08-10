package ru.qwonix.foxwhiskersapi.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;

import java.util.List;

public interface AuthenticationService {

    boolean authenticate(String username, String code);

    void sendCode(String phoneNumber);

    String generateAccessToken(String subject, List<String> authorities);

    String generateRefreshToken(String subject);

    Claims getAccessClaims(String token) throws
            ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, io.jsonwebtoken.security.SignatureException, IllegalArgumentException;

    Claims getRefreshClaims(String token) throws
            ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, io.jsonwebtoken.security.SignatureException, IllegalArgumentException;
}
