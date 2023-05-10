package ru.qwonix.foxwhiskersapi.repository;

import org.springframework.security.core.Authentication;

public interface AuthenticationRepository {

    Authentication authenticate(String phoneNumber, Integer code);

    Boolean sendCode(String phoneNumber);
}
