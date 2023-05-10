package ru.qwonix.foxwhiskersapi.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Getter
public class UpdateException extends ResponseStatusException {

    public UpdateException(HttpStatus status, String reason) {
        super(status, reason);
    }

}
