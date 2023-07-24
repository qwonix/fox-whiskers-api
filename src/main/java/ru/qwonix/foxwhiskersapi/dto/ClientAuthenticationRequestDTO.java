package ru.qwonix.foxwhiskersapi.dto;

public record ClientAuthenticationRequestDTO(String phoneNumber, Integer code) {
}