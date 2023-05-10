package ru.qwonix.foxwhiskersapi.dto;

import lombok.Data;

@Data
public class AuthenticationResponseDTO {
    private final String jwtAccessToken;
    private final String jwtRefreshToken;
}