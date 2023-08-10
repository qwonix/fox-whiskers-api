package ru.qwonix.foxwhiskersapi.service.impl;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
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

    public static final String AUTHORITIES_CLAIM = "AUTHORITIES";

    @Setter
    private Duration authenticationCodeTtl = Duration.ofMinutes(4);

    @Setter
    private Duration accessTtl = Duration.ofDays(1);

    @Setter
    private Duration refreshTtl = Duration.ofDays(30);


    private final Key jwtSecretAccess;
    private final Key jwtSecretRefresh;
    private final ClientService clientService;
    private final AuthenticationRepository authenticationRepository;


    public JwtAuthenticationService(ClientService clientService,
                                    AuthenticationRepository authenticationRepository,
                                    String jwtAccessSecret,
                                    String jwtRefreshSecret
    ) {
        this.clientService = clientService;
        this.authenticationRepository = authenticationRepository;
        this.jwtSecretAccess = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtAccessSecret));
        this.jwtSecretRefresh = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtRefreshSecret));
    }


    @Override
    public boolean authenticate(String username, String code) {
        return authenticationRepository.hasKeyAndValue(username, code);
    }

    @Override
    public void sendCode(String phoneNumber) {
        if (!clientService.exists(phoneNumber)) {
            clientService.save(new Client(phoneNumber));
        }
        RandomGenerator gen = RandomGenerator.of("L128X256MixRandom");
        String code = String.valueOf(gen.nextInt(0000, 10000));
        authenticationRepository.add(phoneNumber, code, authenticationCodeTtl);
    }


    @Override
    public String generateAccessToken(String subject, List<String> authorities) {
        final Instant accessExpirationInstant =
                LocalDateTime.now().plus(accessTtl).atZone(ZoneId.systemDefault()).toInstant();

        return Jwts.builder()
                .setSubject(subject)
                .claim(AUTHORITIES_CLAIM, authorities)
                .setExpiration(Date.from(accessExpirationInstant))
                .signWith(jwtSecretAccess)
                .compact();
    }

    @Override
    public String generateRefreshToken(String subject) {
        final Instant refreshExpirationInstant =
                LocalDateTime.now().plus(refreshTtl).atZone(ZoneId.systemDefault()).toInstant();

        return Jwts.builder()
                .setSubject(subject)
                .setExpiration(Date.from(refreshExpirationInstant))
                .signWith(jwtSecretRefresh)
                .compact();
    }

    @Override
    public Claims getAccessClaims(String token) throws
            ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, io.jsonwebtoken.security.SignatureException, IllegalArgumentException {
        return getClaims(token, jwtSecretAccess);
    }

    @Override
    public Claims getRefreshClaims(String token) throws
            ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, io.jsonwebtoken.security.SignatureException, IllegalArgumentException {
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
