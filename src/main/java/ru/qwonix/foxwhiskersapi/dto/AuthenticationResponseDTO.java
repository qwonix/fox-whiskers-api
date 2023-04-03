package ru.qwonix.foxwhiskersapi.dto;

import lombok.Data;

@Data
public class AuthenticationResponseDTO {
    private String accessToken;
    private String refreshToken;
}