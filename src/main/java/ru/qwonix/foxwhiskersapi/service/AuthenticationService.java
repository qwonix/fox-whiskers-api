package ru.qwonix.foxwhiskersapi.service;

import ru.qwonix.foxwhiskersapi.dto.AuthenticationResponseDTO;
import ru.qwonix.foxwhiskersapi.dto.ClientAuthenticationRequestDTO;
import ru.qwonix.foxwhiskersapi.dto.RefreshJwtRequestDTO;

public interface AuthenticationService {

    AuthenticationResponseDTO authenticate(ClientAuthenticationRequestDTO request);

    AuthenticationResponseDTO refreshTokens(RefreshJwtRequestDTO request);

    void sendCode(String phoneNumber);
}
