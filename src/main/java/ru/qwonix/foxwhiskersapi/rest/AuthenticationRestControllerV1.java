package ru.qwonix.foxwhiskersapi.rest;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.qwonix.foxwhiskersapi.dto.AuthenticationRequestDTO;
import ru.qwonix.foxwhiskersapi.dto.RegistrationRequestDTO;
import ru.qwonix.foxwhiskersapi.entity.User;
import ru.qwonix.foxwhiskersapi.exception.AlreadyExistsException;
import ru.qwonix.foxwhiskersapi.security.JwtAuthenticationProvider;
import ru.qwonix.foxwhiskersapi.service.AuthenticationService;
import ru.qwonix.foxwhiskersapi.service.UserService;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationRestControllerV1 {

    private final JwtAuthenticationProvider jwtAuthenticationProvider;
    private final UserService userService;

    private final AuthenticationService authenticationService;

    public AuthenticationRestControllerV1(JwtAuthenticationProvider jwtAuthenticationProvider, UserService userService, AuthenticationService authenticationService) {
        this.jwtAuthenticationProvider = jwtAuthenticationProvider;
        this.userService = userService;
        this.authenticationService = authenticationService;
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthenticationRequestDTO requestUser) {
        log.info("LOGIN request {}", requestUser);

        // TODO: 03-Apr-23 add UsernamePassword Authentication
        if (authenticationService.canlogin(requestUser.getEmail(), requestUser.getPassword())) {
            User user = userService.findByEmail(requestUser.getEmail()).get();
            String accessToken = jwtAuthenticationProvider.generateAccessToken(user);
            String refreshToken = jwtAuthenticationProvider.generateRefreshToken(user);
            Map<Object, Object> response = new HashMap<>();
            response.put("accessToken", accessToken);
            response.put("refreshToken", refreshToken);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid email/password combination");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegistrationRequestDTO requestUser) {
        log.info("REGISTER request {}", requestUser);
        try {
            authenticationService.register(requestUser);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (AlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username is already taken");
        }
    }
}
