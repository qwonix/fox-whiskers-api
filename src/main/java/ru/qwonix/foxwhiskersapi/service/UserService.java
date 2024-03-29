package ru.qwonix.foxwhiskersapi.service;

import ru.qwonix.foxwhiskersapi.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    User save(User user);

    List<User> getAll();

    Optional<User> findByPhoneNumber(String phoneNumber);

    boolean exists(String phoneNumber);

    User update(User user);
}
