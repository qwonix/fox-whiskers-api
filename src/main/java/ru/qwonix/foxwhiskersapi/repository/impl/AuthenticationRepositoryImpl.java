package ru.qwonix.foxwhiskersapi.repository.impl;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import ru.qwonix.foxwhiskersapi.repository.AuthenticationRepository;
import ru.qwonix.foxwhiskersapi.security.CodeAuthentication;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

@Component
public class AuthenticationRepositoryImpl implements AuthenticationRepository {
    private static final Map<String, Integer> phoneNumberCodeMap = new HashMap<>();

    @Override
    public Authentication authenticate(String phoneNumber, Integer code) {
        boolean contains = false;

        for (Map.Entry<String, Integer> entry : phoneNumberCodeMap.entrySet()) {
            if (Objects.equals(entry.getKey(), phoneNumber) && Objects.equals(entry.getValue(), code)) {
                contains = true;
                return CodeAuthentication.authenticated(phoneNumber);
            }
        }
        return CodeAuthentication.unauthenticated();
    }

    @Override
    public Boolean sendCode(String phoneNumber) {
        int code = new Random().nextInt(8999) + 1000;
        System.out.println("+++++++++ " + phoneNumber + " " + code + "+++++++++");
        phoneNumberCodeMap.put(phoneNumber, code);
        return null;
    }
}
