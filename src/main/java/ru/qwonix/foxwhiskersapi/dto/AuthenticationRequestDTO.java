package ru.qwonix.foxwhiskersapi.dto;

import lombok.Data;

@Data
public class AuthenticationRequestDTO {
    private final String username;
    private final String password;
}