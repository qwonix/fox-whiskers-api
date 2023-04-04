package ru.qwonix.foxwhiskersapi.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Getter
public class RegistrationException extends ResponseStatusException {

    public RegistrationException(HttpStatus status, String reason) {
        super(status, reason);
    }

}
