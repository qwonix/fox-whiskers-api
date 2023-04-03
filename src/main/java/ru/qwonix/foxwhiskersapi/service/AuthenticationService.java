package ru.qwonix.foxwhiskersapi.service;

import ru.qwonix.foxwhiskersapi.dto.RegistrationRequestDTO;
import ru.qwonix.foxwhiskersapi.entity.User;
import ru.qwonix.foxwhiskersapi.exception.AlreadyExistsException;

public interface AuthenticationService {
    User register(RegistrationRequestDTO request) throws AlreadyExistsException;

    boolean canlogin(String email, String password);
}
