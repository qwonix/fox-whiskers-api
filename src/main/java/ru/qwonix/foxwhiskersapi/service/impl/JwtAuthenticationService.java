package ru.qwonix.foxwhiskersapi.service.impl;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.Nonnull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import ru.qwonix.foxwhiskersapi.entity.Permission;
import ru.qwonix.foxwhiskersapi.entity.Role;
import ru.qwonix.foxwhiskersapi.entity.User;
import ru.qwonix.foxwhiskersapi.exception.TokenValidationException;
import ru.qwonix.foxwhiskersapi.repository.AuthenticationRepository;
import ru.qwonix.foxwhiskersapi.security.Token;
import ru.qwonix.foxwhiskersapi.service.AuthenticationService;
import ru.qwonix.foxwhiskersapi.service.UserService;

import java.security.Key;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.random.RandomGenerator;

@Slf4j
public class JwtAuthenticationService implements AuthenticationService {

    private final UserService userService;
    private final AuthenticationRepository authenticationRepository;

    private static final String AUTHORITIES_CLAIM_NAME = "authorities";

    private final Key accessJwtKey;
    private final Key refreshJwtKey;

    @Setter
    private Duration accessTokenTtl = Duration.ofDays(1);
    @Setter
    private Duration refreshTokenTtl = Duration.ofDays(30);

    @Setter
    private Duration authenticationCodeTtl = Duration.ofMinutes(4);


    public JwtAuthenticationService(UserService userService,
                                    AuthenticationRepository authenticationRepository,
                                    @Nonnull Key accessJwtKey,
                                    @Nonnull Key refreshJwtKey
    ) {
        this.userService = userService;
        this.authenticationRepository = authenticationRepository;
        this.accessJwtKey = accessJwtKey;
        this.refreshJwtKey = refreshJwtKey;
    }

    private static int generateCode() {
        RandomGenerator gen = RandomGenerator.of("L128X256MixRandom");
        return gen.nextInt(0000, 9_999 + 1);
    }

    @Override
    public String createAuthenticationCode(String phoneNumber) {
        if (!userService.exists(phoneNumber)) {
            userService.save(new User(phoneNumber, Role.INCOMPLETE_REGISTRATION));
        }
        String code = String.valueOf(generateCode());
        authenticationRepository.add(phoneNumber, code, authenticationCodeTtl);
        return code;
    }


    @Override
    public boolean verifyAuthenticationCode(String username, String code) {
        return authenticationRepository.hasKeyAndValue(username, code);
    }

    @Override
    public Boolean clearAuthenticationCode(String username) {
        return authenticationRepository.delete(username);
    }


    @Override
    public String serializeAccessToken(Token token) {
        return generateToken(token.subject(), token.authorities(), accessJwtKey, accessTokenTtl);
    }

    @Override
    public String serializeRefreshToken(Token token) {
        return generateToken(token.subject(), token.authorities(), refreshJwtKey, refreshTokenTtl);
    }


    /**
     * Generates an JWT access token using the provided subject and authorities
     *
     * @param subject        subject of the token (typically the username)
     * @param authorities    collection of authorities associated with the subject
     * @param accessJwtKey
     * @param accessTokenTtl
     * @return generated JWT access token
     */
    private String generateToken(String subject, Collection<? extends GrantedAuthority> authorities,
                                 Key accessJwtKey,
                                 Duration accessTokenTtl) {
        final var accessExpirationInstant =
                LocalDateTime.now().plus(accessTokenTtl).atZone(ZoneId.systemDefault()).toInstant();

        return Jwts.builder()
                .setSubject(subject)
                .claim(AUTHORITIES_CLAIM_NAME, authorities)
                .setExpiration(Date.from(accessExpirationInstant))
                .signWith(accessJwtKey)
                .compact();
    }


    @Override
    public Token parseAccessToken(String token) throws TokenValidationException {
        return parseToken(token, accessJwtKey);
    }

    @Override
    public Token parseRefreshToken(String token) throws TokenValidationException {
        return parseToken(token, refreshJwtKey);
    }

    /**
     * Parses and validates a JWT token, returning a Token object containing token authentication information
     *
     * @param token  JWT token to parse and validate
     * @param secret secret key used to validate the token's signature
     * @return Token object containing token authentication information
     * @throws TokenValidationException if the token is invalid
     */
    private Token parseToken(String token, Key secret) throws TokenValidationException {
        var claims = parseAndValidateJwt(token, secret);
        var authorities = extractAuthoritiesAndCreateToken(claims);

        return new Token(claims.getSubject(), authorities);
    }

    /**
     * Extracts authorities from the JWT claims and creates a collection of {@link Permission}
     *
     * @param claims The JWT claims containing authority information
     * @return collection of permissions extracted from the claims
     * @throws TokenValidationException If there's an issue with the authority claims
     */
    private Collection<Permission> extractAuthoritiesAndCreateToken(Claims claims) throws TokenValidationException {
        try {
            var stringAuthorities = claims.get(AUTHORITIES_CLAIM_NAME, List.class);
            var authorities = new HashSet<Permission>(stringAuthorities.size());
            for (Object authority : stringAuthorities) {
                var string = authority.toString();
                authorities.add(Permission.valueOf(string));
            }
            return authorities;
        } catch (ClassCastException | IllegalArgumentException e) {
            throw new TokenValidationException("Invalid or missing authorities claim", e);
        }
    }

    /**
     * Parses and validates a JSON Web Token (JWT) using the provided secret key and returns the claims contained in the token
     *
     * @param token  JWT token string to be parsed and validated
     * @param secret secret key used for JWT validation
     * @return claims extracted from the validated JWT
     * @throws TokenValidationException if the token is invalid, expired, or the token string is null/empty/whitespace
     */
    private static Claims parseAndValidateJwt(String token, Key secret) throws TokenValidationException {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secret)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException e) {
            throw new TokenValidationException("Invalid token", e);
        } catch (ExpiredJwtException e) {
            throw new TokenValidationException("Token expired", e);
        } catch (IllegalArgumentException e) {
            throw new TokenValidationException("Token string is null or empty or only whitespace", e);
        }
    }
}