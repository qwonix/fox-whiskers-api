package ru.qwonix.foxwhiskersapi.dto;

import lombok.Data;

@Data
public class RegistrationRequestDTO {
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String middleName;
    private String phoneNumber;
}