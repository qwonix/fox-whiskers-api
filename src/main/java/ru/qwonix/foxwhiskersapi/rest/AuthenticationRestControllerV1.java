package ru.qwonix.foxwhiskersapi.rest;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.qwonix.foxwhiskersapi.dto.CodeAuthenticationRequestDTO;
import ru.qwonix.foxwhiskersapi.service.AuthenticationService;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationRestControllerV1 {

    private final AuthenticationService authenticationService;

    public AuthenticationRestControllerV1(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }


    @PostMapping("/code")
    public ResponseEntity<Boolean> sendAuthenticationCode(@RequestBody CodeAuthenticationRequestDTO request) {
        log.info("send authentication code request from {}", request.phoneNumber());
        authenticationService.createAuthenticationCode(request.phoneNumber());
        return ResponseEntity.ok(true);
    }

}
