package ru.qwonix.foxwhiskersapi.service.impl;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.Nonnull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import ru.qwonix.foxwhiskersapi.entity.Client;
import ru.qwonix.foxwhiskersapi.repository.AuthenticationRepository;
import ru.qwonix.foxwhiskersapi.service.AuthenticationService;
import ru.qwonix.foxwhiskersapi.service.ClientService;

import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.random.RandomGenerator;

@Slf4j
public class JwtAuthenticationService implements AuthenticationService {

    private final Key accessJwtKey;
    private final Key refreshJwtKey;
    private final ClientService clientService;
    private final AuthenticationRepository authenticationRepository;

    @Setter
    private Duration authenticationCodeTtl = Duration.ofMinutes(4);
    @Setter
    private Duration accessTokenTtl = Duration.ofDays(1);
    @Setter
    private Duration refreshTokenTtl = Duration.ofDays(30);

    /**
     *
     * @param clientService
     * @param authenticationRepository
     * @param accessJwtSecret
     * @param refreshJwtSecret
     * @throws io.jsonwebtoken.security.WeakKeyException
     */
    public JwtAuthenticationService(ClientService clientService,
                                    AuthenticationRepository authenticationRepository,
                                    @Nonnull String accessJwtSecret,
                                    @Nonnull String refreshJwtSecret
    ) {
        this(
                clientService,
                authenticationRepository,
                Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessJwtSecret)),
                Keys.hmacShaKeyFor(Decoders.BASE64.decode(refreshJwtSecret))
        );
    }

    public JwtAuthenticationService(ClientService clientService,
                                    AuthenticationRepository authenticationRepository,
                                    @Nonnull Key accessJwtKey,
                                    @Nonnull Key refreshJwtKey
    ) {
        this.clientService = clientService;
        this.authenticationRepository = authenticationRepository;
        this.accessJwtKey = accessJwtKey;
        this.refreshJwtKey = refreshJwtKey;
    }

    @Override
    public boolean verifyCodeAuthentication(String username, String code) {
        return authenticationRepository.hasKeyAndValue(username, code);
    }

    private static int generateCode() {
        RandomGenerator gen = RandomGenerator.of("L128X256MixRandom");
        return gen.nextInt(0000, 10000);
    }

    @Override
    public void sendCode(String phoneNumber) {
        if (!clientService.exists(phoneNumber)) {
            clientService.save(new Client(phoneNumber));
        }
        String code = String.valueOf(generateCode());
        authenticationRepository.add(phoneNumber, code, authenticationCodeTtl);
    }


    @Override
    public String generateAccessToken(String subject, List<String> authorities) {
        final Instant accessExpirationInstant =
                LocalDateTime.now().plus(accessTokenTtl).atZone(ZoneId.systemDefault()).toInstant();

        return Jwts.builder()
                .setSubject(subject)
                .claim(PERMISSIONS_CLAIM, authorities)
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
                .signWith(refreshJwtKey)
                .compact();
    }

    @Override
    public Claims getAccessClaims(String token) throws
            ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, io.jsonwebtoken.security.SignatureException, IllegalArgumentException {
        return getClaims(token, accessJwtKey);
    }

    @Override
    public Claims getRefreshClaims(String token) throws
            ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, io.jsonwebtoken.security.SignatureException, IllegalArgumentException {
        return getClaims(token, refreshJwtKey);
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
