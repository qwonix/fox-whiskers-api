package ru.qwonix.foxwhiskersapi.rest;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.qwonix.foxwhiskersapi.dto.*;
import ru.qwonix.foxwhiskersapi.entity.Client;
import ru.qwonix.foxwhiskersapi.service.AuthenticationService;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationRestControllerV1 {

    private final AuthenticationService authenticationService;

    public AuthenticationRestControllerV1(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponseDTO> authenticate(
            @RequestBody AuthenticationRequestDTO request
    ) {
        log.info("AUTHENTICATE request {}", request);
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthenticationResponseDTO> refresh(
            @RequestBody RefreshJwtRequestDTO request
    ) {
        log.info("REFRESH request {}", request);
        return ResponseEntity.ok(authenticationService.refresh(request));
    }

    @PostMapping("/code")
    public ResponseEntity<Boolean> code(@RequestBody CodeAuthenticationRequest request) {
        log.info("CODE request {}", request.getPhoneNumber());
        authenticationService.sendCode(request.getPhoneNumber());
        return ResponseEntity.ok(true);
    }

}
