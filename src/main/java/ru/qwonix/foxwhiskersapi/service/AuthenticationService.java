package ru.qwonix.foxwhiskersapi.service;

import ru.qwonix.foxwhiskersapi.dto.*;
import ru.qwonix.foxwhiskersapi.entity.Client;
import ru.qwonix.foxwhiskersapi.security.NoPasswordAuthentication;

public interface AuthenticationService {

    AuthenticationResponseDTO authenticate(ClientAuthenticationRequestDTO request);

    AuthenticationResponseDTO refresh(RefreshJwtRequestDTO request);

    NoPasswordAuthentication loadUserByUsername(String username);

    Client update(UpdateClientDTO request);

    Boolean sendCode(String phoneNumber);
}
