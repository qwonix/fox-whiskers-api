package ru.qwonix.foxwhiskersapi.dto;

import lombok.Data;

@Data
public class ClientAuthenticationRequestDTO {
    private final String phoneNumber;
    private final Integer code;
}