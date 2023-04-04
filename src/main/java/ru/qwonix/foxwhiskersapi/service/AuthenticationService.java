package ru.qwonix.foxwhiskersapi.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import ru.qwonix.foxwhiskersapi.dto.AuthenticationRequestDTO;
import ru.qwonix.foxwhiskersapi.dto.AuthenticationResponseDTO;
import ru.qwonix.foxwhiskersapi.dto.RefreshJwtRequestDTO;
import ru.qwonix.foxwhiskersapi.dto.RegistrationRequestDTO;
import ru.qwonix.foxwhiskersapi.entity.User;

public interface AuthenticationService extends UserDetailsService {
    User register(RegistrationRequestDTO request);

    AuthenticationResponseDTO login(AuthenticationRequestDTO requestUser);

    AuthenticationResponseDTO refresh(RefreshJwtRequestDTO request);
}
