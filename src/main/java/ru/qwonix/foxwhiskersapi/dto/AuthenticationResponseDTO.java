package ru.qwonix.foxwhiskersapi.dto;

public record AuthenticationResponseDTO(String jwtAccessToken, String jwtRefreshToken) {
}