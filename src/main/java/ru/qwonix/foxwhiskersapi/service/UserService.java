package ru.qwonix.foxwhiskersapi.service;

import ru.qwonix.foxwhiskersapi.dto.RegistrationRequestDTO;
import ru.qwonix.foxwhiskersapi.entity.User;
import ru.qwonix.foxwhiskersapi.exception.AlreadyExistsException;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User register(RegistrationRequestDTO request) throws AlreadyExistsException;

    List<User> getAll();

    Optional<User> findByEmail(String username);

    User findById(Long id);

    void delete(Long id);

}
