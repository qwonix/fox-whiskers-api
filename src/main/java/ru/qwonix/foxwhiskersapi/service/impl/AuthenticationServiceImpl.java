package ru.qwonix.foxwhiskersapi.service.impl;

import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.qwonix.foxwhiskersapi.dto.ClientAuthenticationRequestDTO;
import ru.qwonix.foxwhiskersapi.dto.AuthenticationResponseDTO;
import ru.qwonix.foxwhiskersapi.dto.RefreshJwtRequestDTO;
import ru.qwonix.foxwhiskersapi.dto.UpdateClientDTO;
import ru.qwonix.foxwhiskersapi.entity.Client;
import ru.qwonix.foxwhiskersapi.exception.JwtAuthenticationException;
import ru.qwonix.foxwhiskersapi.exception.UpdateException;
import ru.qwonix.foxwhiskersapi.repository.AuthenticationRepository;
import ru.qwonix.foxwhiskersapi.security.JwtAuthenticationProvider;
import ru.qwonix.foxwhiskersapi.security.NoPasswordAuthentication;
import ru.qwonix.foxwhiskersapi.service.AuthenticationService;
import ru.qwonix.foxwhiskersapi.service.ClientService;

import java.util.Optional;

@Slf4j
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final ClientService clientService;
    private final AuthenticationRepository authenticationRepository;
    private final JwtAuthenticationProvider jwtAuthenticationProvider;

    public AuthenticationServiceImpl(ClientService clientService,
                                     AuthenticationRepository authenticationRepository,
                                     @Lazy JwtAuthenticationProvider jwtAuthenticationProvider) {
        this.clientService = clientService;
        this.authenticationRepository = authenticationRepository;
        this.jwtAuthenticationProvider = jwtAuthenticationProvider;
    }

    @Override
    public AuthenticationResponseDTO authenticate(ClientAuthenticationRequestDTO request) {
        String phoneNumber = request.getPhoneNumber();
        Integer code = request.getCode();

        Authentication authenticate = authenticationRepository.authenticate(phoneNumber, code);

        if (authenticate.isAuthenticated()) {
//            Client client = Client.builder()
//                    .phoneNumber(phoneNumber).build();
//            userService.save(client);

            String accessToken = jwtAuthenticationProvider.generateAccessToken(phoneNumber);
            String refreshToken = jwtAuthenticationProvider.generateRefreshToken(phoneNumber);
            return new AuthenticationResponseDTO(accessToken, refreshToken);
        } else {
            throw new BadCredentialsException("Invalid username/code combination");
        }
    }

    @Override
    public Boolean sendCode(String phoneNumber) {
        Optional<Client> clientOptional = clientService.findByPhoneNumber(phoneNumber);
        if (!clientOptional.isPresent()) {
            clientService.save(Client.builder()
                    .phoneNumber(phoneNumber)
                    .build());
        }
        return authenticationRepository.sendCode(phoneNumber);
    }

    @Override
    public AuthenticationResponseDTO refresh(RefreshJwtRequestDTO request) {
        // TODO: 04-Apr-23 add refresh token saving and revoking
        try {
            String token = request.getRefreshToken();
            jwtAuthenticationProvider.validateRefreshToken(token);
            String username = jwtAuthenticationProvider.getRefreshClaims(token).getSubject();

            NoPasswordAuthentication client = loadUserByUsername(username);

            String accessToken = jwtAuthenticationProvider.generateAccessToken(client.getUsername());
            String refreshToken = jwtAuthenticationProvider.generateRefreshToken(client.getUsername());
            return new AuthenticationResponseDTO(accessToken, refreshToken);
        } catch (JwtException | UsernameNotFoundException e) {
            throw new JwtAuthenticationException("Invalid refresh JWT token", e);
        }
    }

    @Override
    public NoPasswordAuthentication loadUserByUsername(String username) {
        return clientService.findByPhoneNumber(username).orElseThrow(() -> new UsernameNotFoundException("client"));
    }

    @Override
    public Client update(UpdateClientDTO request) {
        Optional<Client> optionalClient = clientService.findByPhoneNumber(request.getPhoneNumber());

        if (optionalClient.isPresent()) {
            Client client = optionalClient.get();

            client.setFirstName(request.getFirstName());
            client.setLastName(request.getLastName());
            client.setEmail(request.getEmail());

            Client updatedClient = clientService.save(client);

            return updatedClient;
        } else {
            throw new UpdateException(HttpStatus.CONFLICT, "client with username " + request.getPhoneNumber() + " not exists");
        }
    }
}

