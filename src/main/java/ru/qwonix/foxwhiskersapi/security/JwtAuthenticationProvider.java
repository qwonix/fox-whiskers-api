package ru.qwonix.foxwhiskersapi.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import ru.qwonix.foxwhiskersapi.exception.JwtAuthenticationException;
import ru.qwonix.foxwhiskersapi.service.UserService;

import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Slf4j
@Component
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final UserService userService;

    private final Key jwtSecretAccess;
    private final Key jwtSecretRefresh;


    @Value("${jwt.expiration.access}")
    private Duration accessExpiration;
    @Value("${jwt.expiration.refresh}")
    private Duration refreshExpiration;


    public JwtAuthenticationProvider(UserService userService,
                                     @Value("${jwt.secret.access}") String jwtAccessSecret,
                                     @Value("${jwt.secret.refresh}") String jwtRefreshSecret) {
        this.userService = userService;
        this.jwtSecretAccess = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtAccessSecret));
        this.jwtSecretRefresh = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtRefreshSecret));
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Assert.isInstanceOf(JwtAuthenticationToken.class, authentication);

        String token = determineToken(authentication);
        if (!validateAccessToken(token)) {
            throw new JwtAuthenticationException("Invalid JWT token", HttpStatus.UNAUTHORIZED);
        }

        String username = getAccessClaims(token).getSubject();
        UserDetails userDetails = userService.findByEmail(username).orElseThrow(() ->
                new UsernameNotFoundException("User with email " + username + " not found"));

        return JwtAuthenticationToken.authenticated(token, username, userDetails.getAuthorities());
    }

    private String determineToken(Authentication authentication) {
        return (authentication.getCredentials() == null) ? "NONE_PROVIDED" : authentication.getCredentials().toString();
    }

    public String generateAccessToken(UserDetails user) {
        final LocalDateTime now = LocalDateTime.now();
        final Instant accessExpirationInstant =
                now.plus(accessExpiration).atZone(ZoneId.systemDefault()).toInstant();

        return Jwts.builder()
                .setSubject(user.getUsername())
                .setExpiration(Date.from(accessExpirationInstant))
                .signWith(jwtSecretAccess)
                .compact();
    }

    public String generateRefreshToken(UserDetails user) {
        final LocalDateTime now = LocalDateTime.now();
        final Instant refreshExpirationInstant =
                now.plus(refreshExpiration).atZone(ZoneId.systemDefault()).toInstant();

        return Jwts.builder()
                .setSubject(user.getUsername())
                .setExpiration(Date.from(refreshExpirationInstant))
                .signWith(jwtSecretRefresh)
                .compact();
    }

    public boolean validateAccessToken(String accessToken) {
        return validateToken(accessToken, jwtSecretAccess);
    }

    public boolean validateRefreshToken(String refreshToken) {
        return validateToken(refreshToken, jwtSecretRefresh);
    }

    private boolean validateToken(String token, Key secret) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secret)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Claims getAccessClaims(String token) {
        return getClaims(token, jwtSecretAccess);
    }

    public Claims getRefreshClaims(String token) {
        return getClaims(token, jwtSecretRefresh);
    }

    private Claims getClaims(String token, Key secret) {
        return Jwts.parserBuilder()
                .setSigningKey(secret)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}