package ru.qwonix.foxwhiskersapi.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.qwonix.foxwhiskersapi.exception.JwtAuthenticationException;
import ru.qwonix.foxwhiskersapi.exception.UpdateException;


@RestControllerAdvice
public class AuthenticationExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(JwtAuthenticationException.class)
    public ResponseEntity<?> handleJwtAuthenticationException(JwtAuthenticationException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }

    @ExceptionHandler(UpdateException.class)
    public ResponseEntity<?> handleRegistrationException(UpdateException e) {
        return ResponseEntity.status(e.getStatus()).body(e.getReason());
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<?> handleUsernameNotFoundException(UsernameNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

}
