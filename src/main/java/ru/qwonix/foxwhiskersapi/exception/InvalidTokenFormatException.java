package ru.qwonix.foxwhiskersapi.exception;

import org.springframework.security.core.AuthenticationException;

public class InvalidTokenFormatException extends AuthenticationException {

    public InvalidTokenFormatException(String msg) {
        super(msg);
    }

    public InvalidTokenFormatException(String msg, Throwable t) {
        super(msg, t);
    }
}
