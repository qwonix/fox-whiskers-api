package ru.qwonix.foxwhiskersapi.dto;

import lombok.Data;

@Data
public class CodeAuthenticationRequest {
    private final String phoneNumber;
}
