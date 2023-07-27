package ru.qwonix.foxwhiskersapi.repository;

import org.springframework.security.core.Authentication;

public interface AuthenticationRepository {

    Authentication authenticate(String phoneNumber, Integer code);

    Boolean sendCode(String phoneNumber);

    String generateAccessToken(String phoneNumber);
    String generateRefreshToken(String phoneNumber);

    String getSubjectFromTokenClaims(String refreshToken);

    Boolean revokeRefreshToken(String refreshToken);
}
