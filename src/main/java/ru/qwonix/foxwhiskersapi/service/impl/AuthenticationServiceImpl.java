package ru.qwonix.foxwhiskersapi.service.impl;

import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.qwonix.foxwhiskersapi.dto.AuthenticationResponseDTO;
import ru.qwonix.foxwhiskersapi.dto.ClientAuthenticationRequestDTO;
import ru.qwonix.foxwhiskersapi.dto.RefreshJwtRequestDTO;
import ru.qwonix.foxwhiskersapi.dto.UpdateClientDTO;
import ru.qwonix.foxwhiskersapi.entity.Client;
import ru.qwonix.foxwhiskersapi.exception.JwtAuthenticationException;
import ru.qwonix.foxwhiskersapi.exception.UpdateException;
import ru.qwonix.foxwhiskersapi.repository.AuthenticationRepository;
import ru.qwonix.foxwhiskersapi.security.NoPasswordAuthentication;
import ru.qwonix.foxwhiskersapi.service.AuthenticationService;
import ru.qwonix.foxwhiskersapi.service.ClientService;

import java.util.Optional;

@Slf4j
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final ClientService clientService;
    private final AuthenticationRepository authenticationRepository;

    public AuthenticationServiceImpl(ClientService clientService,
                                     AuthenticationRepository authenticationRepository) {
        this.clientService = clientService;
        this.authenticationRepository = authenticationRepository;
    }

    @Override
    public AuthenticationResponseDTO authenticate(ClientAuthenticationRequestDTO request) {
        String phoneNumber = request.phoneNumber();
        Integer code = request.code();

        Authentication authentication = authenticationRepository.authenticate(phoneNumber, code);

        if (authentication.isAuthenticated()) {
            String accessToken = authenticationRepository.generateAccessToken(phoneNumber);
            String refreshToken = authenticationRepository.generateRefreshToken(phoneNumber);
            return new AuthenticationResponseDTO(accessToken, refreshToken);
        } else {
            throw new BadCredentialsException("Invalid username/code combination");
        }
    }

    @Override
    public void sendCode(String phoneNumber) {
        if (!clientService.exists(phoneNumber)) {
            clientService.save(new Client(phoneNumber));
        }
        authenticationRepository.sendCode(phoneNumber);
    }

    @Override
    public AuthenticationResponseDTO refreshTokens(RefreshJwtRequestDTO request) {
        // TODO: 04-Apr-23 add refresh token saving and revoking
        String refreshToken = request.refreshToken();
        try {
            String subject = authenticationRepository.getSubjectFromTokenClaims(refreshToken);

            if (authenticationRepository.revokeRefreshToken(refreshToken)) {
                String newAccessToken = authenticationRepository.generateAccessToken(subject);
                String newRefreshToken = authenticationRepository.generateRefreshToken(subject);
                return new AuthenticationResponseDTO(newAccessToken, newRefreshToken);
            }
            throw new JwtAuthenticationException("Can't revoke refresh token");
        } catch (JwtException | UsernameNotFoundException e) {
            throw new JwtAuthenticationException("Invalid refresh JWT token", e);
        }
    }
}
