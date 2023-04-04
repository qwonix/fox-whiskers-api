package ru.qwonix.foxwhiskersapi.dto;

import lombok.Data;

@Data
public class RefreshJwtRequestDTO {
    private final String refreshToken;
}