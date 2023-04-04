package ru.qwonix.foxwhiskersapi.rest;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.qwonix.foxwhiskersapi.dto.AuthenticationRequestDTO;
import ru.qwonix.foxwhiskersapi.dto.AuthenticationResponseDTO;
import ru.qwonix.foxwhiskersapi.dto.RefreshJwtRequestDTO;
import ru.qwonix.foxwhiskersapi.dto.RegistrationRequestDTO;
import ru.qwonix.foxwhiskersapi.entity.User;
import ru.qwonix.foxwhiskersapi.service.AuthenticationService;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationRestControllerV1 {

    private final AuthenticationService authenticationService;

    public AuthenticationRestControllerV1(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponseDTO> login(@RequestBody AuthenticationRequestDTO request) {
        log.info("LOGIN request {}", request);
        return ResponseEntity.ok(authenticationService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegistrationRequestDTO request) {
        log.info("REGISTER request {}", request);
        User registeredUser = authenticationService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshJwtRequestDTO request) {
        log.info("REFRESH request {}", request);
        return ResponseEntity.ok(authenticationService.refresh(request));
    }
}
