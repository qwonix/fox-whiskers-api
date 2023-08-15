package ru.qwonix.foxwhiskersapi.dto;

public record UpdateUserDTO(String phoneNumber, String email, String firstName, String lastName, String middleName) {
}
