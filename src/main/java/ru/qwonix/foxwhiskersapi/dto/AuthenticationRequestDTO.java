package ru.qwonix.foxwhiskersapi.dto;

import lombok.Data;

@Data
public class AuthenticationRequestDTO {
    private final String phoneNumber;
    private final Integer code;
}