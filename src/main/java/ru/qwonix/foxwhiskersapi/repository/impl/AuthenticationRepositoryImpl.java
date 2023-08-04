package ru.qwonix.foxwhiskersapi.repository.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Repository;
import ru.qwonix.foxwhiskersapi.entity.Role;
import ru.qwonix.foxwhiskersapi.repository.AuthenticationRepository;
import ru.qwonix.foxwhiskersapi.repository.RedisRepository;
import ru.qwonix.foxwhiskersapi.security.CodeAuthentication;
import ru.qwonix.foxwhiskersapi.security.JwtAuthenticationProvider;

import java.time.Duration;
import java.util.random.RandomGenerator;

@Repository
public class AuthenticationRepositoryImpl implements AuthenticationRepository {

    private final JwtAuthenticationProvider jwtAuthenticationProvider;

    private final Duration authenticationCodeTtl;
    private final Duration refreshTokenTtl;

    private final RedisRepository redisRepository;


    public AuthenticationRepositoryImpl(JwtAuthenticationProvider jwtAuthenticationProvider,
                                        @Value("${authentication.code.ttl}") Duration authenticationCodeTtl,
                                        @Value("${jwt.ttl.refresh}") Duration refreshTtl, RedisRepository redisRepository) {
        this.jwtAuthenticationProvider = jwtAuthenticationProvider;
        this.authenticationCodeTtl = authenticationCodeTtl;
        this.refreshTokenTtl = refreshTtl;
        this.redisRepository = redisRepository;
    }

    @Override
    public Authentication authenticate(String phoneNumber, String code) {
        if (redisRepository.hasKeyAndValue(phoneNumber, code)) {
            return CodeAuthentication.authenticated(phoneNumber);
        } else return CodeAuthentication.unauthenticated();
    }

    @Override
    public Boolean sendCode(String phoneNumber) {
        RandomGenerator gen = RandomGenerator.of("L128X256MixRandom");
        String code = String.valueOf(gen.nextInt(0000, 10000));
        redisRepository.add(phoneNumber, code, authenticationCodeTtl);
        return true;
    }

    @Override
    public String generateAccessToken(String phoneNumber, Role role) {
        return jwtAuthenticationProvider.generateAccessToken(phoneNumber, String.valueOf(role));
    }

    @Override
    public String generateRefreshToken(String phoneNumber) {
        String refreshToken = jwtAuthenticationProvider.generateRefreshToken(phoneNumber);
        redisRepository.add(refreshToken, phoneNumber, refreshTokenTtl);
        return refreshToken;
    }

    @Override
    public String getSubjectFromRefreshTokenClaims(String refreshToken) {
        return jwtAuthenticationProvider.getRefreshClaims(refreshToken).getSubject();
    }

    @Override
    public Boolean revokeRefreshToken(String refreshToken) {
        return redisRepository.delete(refreshToken);
    }
}
