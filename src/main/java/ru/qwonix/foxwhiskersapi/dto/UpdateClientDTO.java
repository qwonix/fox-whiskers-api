package ru.qwonix.foxwhiskersapi.dto;

import lombok.Data;

@Data
public class UpdateClientDTO {
    private final String phoneNumber;
    private final String firstName;
    private final String lastName;
    private final String email;
}
