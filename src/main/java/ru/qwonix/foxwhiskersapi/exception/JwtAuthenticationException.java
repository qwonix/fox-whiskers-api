package ru.qwonix.foxwhiskersapi.exception;

import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

@Getter
public class JwtAuthenticationException extends AuthenticationException {
    public JwtAuthenticationException(String msg, Throwable cause) {
        super(msg, cause);
    }
}