package ru.qwonix.foxwhiskersapi.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.Assert;
import ru.qwonix.foxwhiskersapi.exception.JwtAuthenticationException;
import ru.qwonix.foxwhiskersapi.service.AuthenticationService;

import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Slf4j
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final AuthenticationService authenticationService;

    private final Key jwtSecretAccess;
    private final Key jwtSecretRefresh;

    private Duration accessExpiration;
    private Duration refreshExpiration;

    public JwtAuthenticationProvider(AuthenticationService authenticationService, String jwtAccessSecret, String jwtRefreshSecret) {
        this.authenticationService = authenticationService;
        this.jwtSecretAccess = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtAccessSecret));
        this.jwtSecretRefresh = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtRefreshSecret));
        this.accessExpiration = Duration.ofDays(1);
        this.refreshExpiration = Duration.ofDays(30);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws JwtAuthenticationException {
        Assert.isInstanceOf(JwtAuthenticationToken.class, authentication);

        String token = determineToken(authentication);
        String username;
        try {
            validateAccessToken(token);
            username = getAccessClaims(token).getSubject();
            NoPasswordAuthentication userDetails = authenticationService.loadUserByUsername(username);

            return JwtAuthenticationToken.authenticated(token, username, userDetails.getAuthorities());
        } catch (JwtException | UsernameNotFoundException e) {
            throw new JwtAuthenticationException("Invalid JWT token", e);
        }
    }

    private String determineToken(Authentication authentication) {
        return (authentication.getCredentials() == null) ? "NONE_PROVIDED" : authentication.getCredentials().toString();
    }

    public String generateAccessToken(String subject) {
        final LocalDateTime now = LocalDateTime.now();
        final Instant accessExpirationInstant =
                now.plus(accessExpiration).atZone(ZoneId.systemDefault()).toInstant();

        return Jwts.builder()
                .setSubject(subject)
                .setExpiration(Date.from(accessExpirationInstant))
                .signWith(jwtSecretAccess)
                .compact();
    }

    public String generateRefreshToken(String subject) {
        final LocalDateTime now = LocalDateTime.now();
        final Instant refreshExpirationInstant =
                now.plus(refreshExpiration).atZone(ZoneId.systemDefault()).toInstant();

        return Jwts.builder()
                .setSubject(subject)
                .setExpiration(Date.from(refreshExpirationInstant))
                .signWith(jwtSecretRefresh)
                .compact();
    }

    public boolean validateAccessToken(String accessToken) throws
            ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, SignatureException, IllegalArgumentException {
        return validateToken(accessToken, jwtSecretAccess);
    }

    public boolean validateRefreshToken(String refreshToken) throws
            ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, SignatureException, IllegalArgumentException {
        return validateToken(refreshToken, jwtSecretRefresh);
    }

    private boolean validateToken(String token, Key secret) throws
            ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, SignatureException, IllegalArgumentException {
        Jwts.parserBuilder()
                .setSigningKey(secret)
                .build()
                .parseClaimsJws(token);

        return true;
    }

    public Claims getAccessClaims(String token) throws
            ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, SignatureException, IllegalArgumentException {
        return getClaims(token, jwtSecretAccess);
    }

    public Claims getRefreshClaims(String token) throws
            ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, SignatureException, IllegalArgumentException {
        return getClaims(token, jwtSecretRefresh);
    }

    private Claims getClaims(String token, Key secret) throws
            ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, SignatureException, IllegalArgumentException {
        return Jwts.parserBuilder()
                .setSigningKey(secret)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public void setAccessExpiration(Duration accessExpiration) {
        this.accessExpiration = accessExpiration;
    }

    public void setRefreshExpiration(Duration refreshExpiration) {
        this.refreshExpiration = refreshExpiration;
    }
}