package ru.qwonix.foxwhiskersapi.repository;

import org.springframework.security.core.Authentication;
import ru.qwonix.foxwhiskersapi.entity.Role;

public interface AuthenticationRepository {

    Authentication authenticate(String phoneNumber, String code);

    Boolean sendCode(String phoneNumber);

    String generateAccessToken(String phoneNumber, Role role);

    String generateRefreshToken(String phoneNumber);

    String getSubjectFromRefreshTokenClaims(String refreshToken);

    Boolean revokeRefreshToken(String refreshToken);
}
