package ru.qwonix.foxwhiskersapi.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.Assert;
import ru.qwonix.foxwhiskersapi.entity.Role;
import ru.qwonix.foxwhiskersapi.exception.JwtAuthenticationException;

import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Slf4j
public class JwtAuthenticationProvider implements AuthenticationProvider {

    public static final String ROLE_CLAIM = "role";

    private final Key jwtSecretAccess;
    private final Key jwtSecretRefresh;

    @Setter
    private Duration accessExpiration = Duration.ofDays(1);

    @Setter
    private Duration refreshExpiration = Duration.ofDays(30);

    public JwtAuthenticationProvider(Key jwtSecretAccess, Key jwtSecretRefresh) {
        this.jwtSecretAccess = jwtSecretAccess;
        this.jwtSecretRefresh = jwtSecretRefresh;
    }

    public JwtAuthenticationProvider(Key jwtSecretAccess, Key jwtSecretRefresh, Duration accessExpiration, Duration refreshExpiration) {
        this(jwtSecretAccess, jwtSecretRefresh);
        this.setAccessExpiration(accessExpiration);
        this.setRefreshExpiration(refreshExpiration);
    }

    public JwtAuthenticationProvider(String jwtSecretAccess, String jwtSecretRefresh) {
        this(Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecretAccess)), Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecretRefresh)));
    }

    public JwtAuthenticationProvider() {
        this(Keys.secretKeyFor(SignatureAlgorithm.HS256), Keys.secretKeyFor(SignatureAlgorithm.HS256));
    }

    public JwtAuthenticationProvider(Duration accessExpiration, Duration refreshExpiration) {
        this(Keys.secretKeyFor(SignatureAlgorithm.HS256), Keys.secretKeyFor(SignatureAlgorithm.HS256), accessExpiration, refreshExpiration);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws JwtAuthenticationException {
        Assert.isInstanceOf(JwtAuthenticationToken.class, authentication);
        String token = determineToken(authentication);

        try {
            var accessClaims = getAccessClaims(token);
            String username = accessClaims.getSubject();
            String role = (String) accessClaims.get(ROLE_CLAIM);

            return JwtAuthenticationToken.authenticated(token, username, Role.valueOf(role).getAuthorities());
        } catch (JwtException | UsernameNotFoundException e) {
            throw new JwtAuthenticationException("Invalid JWT token", e);
        }
    }

    private String determineToken(Authentication authentication) {
        return (authentication.getCredentials() == null) ? "NONE_PROVIDED" : authentication.getCredentials().toString();
    }

    public String generateAccessToken(String subject, String role) {
        final LocalDateTime now = LocalDateTime.now();
        final Instant accessExpirationInstant =
                now.plus(accessExpiration).atZone(ZoneId.systemDefault()).toInstant();

        return Jwts.builder()
                .setSubject(subject)
                .claim(ROLE_CLAIM, role)
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
}