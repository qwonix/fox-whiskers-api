package ru.qwonix.foxwhiskersapi.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Client {
    private UUID id;
    private String phoneNumber;
    private String email;
    private String firstName;
    private String lastName;
    private String middleName;

    private UserStatus status;
    private Role role;
    private LocalDateTime created;
    private LocalDateTime updated;

    public Client(String phoneNumber, Role role) {
        this.phoneNumber = phoneNumber;
        this.role = role;
    }
}