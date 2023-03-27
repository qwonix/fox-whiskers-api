package ru.qwonix.foxwhiskersapi.rest;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.qwonix.foxwhiskersapi.dto.AuthenticationRequestDTO;
import ru.qwonix.foxwhiskersapi.dto.RegistrationRequestDTO;
import ru.qwonix.foxwhiskersapi.entity.User;
import ru.qwonix.foxwhiskersapi.exception.UserAlreadyExistsException;
import ru.qwonix.foxwhiskersapi.security.JwtTokenProvider;
import ru.qwonix.foxwhiskersapi.service.UserService;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationRestControllerV1 {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthenticationRestControllerV1(AuthenticationManager authenticationManager, UserService userService, JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthenticationRequestDTO requestUser) {
        log.info("LOGIN request {}", requestUser);
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(requestUser.getEmail(), requestUser.getPassword()));
            userService.findByEmail(requestUser.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("User doesn't exists"));

            String token = jwtTokenProvider.createToken(requestUser.getEmail());
            Map<Object, Object> response = new HashMap<>();
            response.put("token", token);
            return ResponseEntity.ok(response);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid email/password combination");
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid email/password combination");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegistrationRequestDTO requestUser) {
        log.info("REGISTER request {}", requestUser);
        try {
            User registeredUser = userService.register(requestUser);
            return ResponseEntity.ok().body(registeredUser);
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username is already taken");
        }
    }
}
