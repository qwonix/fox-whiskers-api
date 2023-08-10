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
    private UserStatus status;
    private String firstName;
    private String lastName;
    private String middleName;
    private String email;
    private LocalDateTime created;
    private LocalDateTime updated;
    public Client(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}