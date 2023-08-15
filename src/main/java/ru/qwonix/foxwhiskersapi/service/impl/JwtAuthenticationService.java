package ru.qwonix.foxwhiskersapi.service.impl;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.Nonnull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import ru.qwonix.foxwhiskersapi.entity.User;
import ru.qwonix.foxwhiskersapi.entity.Permission;
import ru.qwonix.foxwhiskersapi.entity.Role;
import ru.qwonix.foxwhiskersapi.repository.AuthenticationRepository;
import ru.qwonix.foxwhiskersapi.security.Token;
import ru.qwonix.foxwhiskersapi.service.AuthenticationService;
import ru.qwonix.foxwhiskersapi.service.UserService;

import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.random.RandomGenerator;

@Slf4j
public class JwtAuthenticationService implements AuthenticationService {

    private final Key accessJwtKey;
    private final Key refreshJwtKey;
    private final UserService userService;
    private final AuthenticationRepository authenticationRepository;

    @Setter
    private Duration authenticationCodeTtl = Duration.ofMinutes(4);
    @Setter
    private Duration accessTokenTtl = Duration.ofDays(1);
    @Setter
    private Duration refreshTokenTtl = Duration.ofDays(30);

    /**
     * @param userService
     * @param authenticationRepository
     * @param accessJwtSecret
     * @param refreshJwtSecret
     * @throws io.jsonwebtoken.security.WeakKeyException
     */
    public JwtAuthenticationService(UserService userService,
                                    AuthenticationRepository authenticationRepository,
                                    @Nonnull String accessJwtSecret,
                                    @Nonnull String refreshJwtSecret
    ) {
        this(
                userService,
                authenticationRepository,
                Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessJwtSecret)),
                Keys.hmacShaKeyFor(Decoders.BASE64.decode(refreshJwtSecret))
        );
    }

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
        return gen.nextInt(0000, 10000);
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
    public String generateAccessToken(String subject, Collection<? extends GrantedAuthority> authorities) {
        final Instant accessExpirationInstant =
                LocalDateTime.now().plus(accessTokenTtl).atZone(ZoneId.systemDefault()).toInstant();
        var permissions = authorities.stream().map(GrantedAuthority::getAuthority).toList();

        return Jwts.builder()
                .setSubject(subject)
                .claim(PERMISSIONS_CLAIM, permissions)
                .setExpiration(Date.from(accessExpirationInstant))
                .signWith(accessJwtKey)
                .compact();
    }

    @Override
    public String generateRefreshToken(String subject) {
        final Instant refreshExpirationInstant =
                LocalDateTime.now().plus(refreshTokenTtl).atZone(ZoneId.systemDefault()).toInstant();

        return Jwts.builder()
                .setSubject(subject)
                .setExpiration(Date.from(refreshExpirationInstant))
                .claim(PERMISSIONS_CLAIM, Set.of(Permission.TOKEN_REFRESH, Permission.TOKEN_LOGOUT))
                .signWith(refreshJwtKey)
                .compact();
    }

    @Override
    public Token getAccessToken(String token) throws
            ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, io.jsonwebtoken.security.SignatureException, IllegalArgumentException {
        return getToken(token, accessJwtKey);
    }

    @Override
    public Token getRefreshToken(String token) throws
            ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, io.jsonwebtoken.security.SignatureException, IllegalArgumentException {
        return getToken(token, refreshJwtKey);
    }

    private Token getToken(String token, Key secret) throws
            ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, SignatureException, IllegalArgumentException {
        Claims body = Jwts.parserBuilder()
                .setSigningKey(secret)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return new Token(body.getSubject(), body.get(PERMISSIONS_CLAIM, List.class).stream().map(it -> Permission.valueOf(it.toString())).toList());
    }

}
