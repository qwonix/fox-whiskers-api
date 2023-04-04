package ru.qwonix.foxwhiskersapi.service.impl;

import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.qwonix.foxwhiskersapi.dto.AuthenticationRequestDTO;
import ru.qwonix.foxwhiskersapi.dto.AuthenticationResponseDTO;
import ru.qwonix.foxwhiskersapi.dto.RefreshJwtRequestDTO;
import ru.qwonix.foxwhiskersapi.dto.RegistrationRequestDTO;
import ru.qwonix.foxwhiskersapi.entity.ClientDetails;
import ru.qwonix.foxwhiskersapi.entity.Role;
import ru.qwonix.foxwhiskersapi.entity.User;
import ru.qwonix.foxwhiskersapi.entity.UserStatus;
import ru.qwonix.foxwhiskersapi.exception.JwtAuthenticationException;
import ru.qwonix.foxwhiskersapi.exception.RegistrationException;
import ru.qwonix.foxwhiskersapi.security.JwtAuthenticationProvider;
import ru.qwonix.foxwhiskersapi.service.AuthenticationService;
import ru.qwonix.foxwhiskersapi.service.UserService;

@Slf4j
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtAuthenticationProvider jwtAuthenticationProvider;

    public AuthenticationServiceImpl(UserService userService,
                                     PasswordEncoder passwordEncoder,
                                     @Lazy AuthenticationManager authenticationManager,
                                     @Lazy JwtAuthenticationProvider jwtAuthenticationProvider) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtAuthenticationProvider = jwtAuthenticationProvider;
    }

    @Override
    public AuthenticationResponseDTO login(AuthenticationRequestDTO requestUser) {
        UserDetails userDetails = loadUserByUsername(requestUser.getUsername());
        UsernamePasswordAuthenticationToken authentication =
                UsernamePasswordAuthenticationToken.unauthenticated(userDetails, requestUser.getPassword());

        Authentication authenticate = authenticationManager.authenticate(authentication);

        if (authenticate.isAuthenticated()) {
            String accessToken = jwtAuthenticationProvider.generateAccessToken(userDetails);
            String refreshToken = jwtAuthenticationProvider.generateRefreshToken(userDetails);
            return new AuthenticationResponseDTO(accessToken, refreshToken);
        } else {
            throw new BadCredentialsException("Invalid username/password combination");
        }
    }

    @Override
    public AuthenticationResponseDTO refresh(RefreshJwtRequestDTO request) {
        // TODO: 04-Apr-23 add refresh token saving and revoking
        try {
            String token = request.getRefreshToken();
            jwtAuthenticationProvider.validateRefreshToken(token);
            String username = jwtAuthenticationProvider.getRefreshClaims(token).getSubject();

            UserDetails userDetails = loadUserByUsername(username);

            String accessToken = jwtAuthenticationProvider.generateAccessToken(userDetails);
            String refreshToken = jwtAuthenticationProvider.generateRefreshToken(userDetails);
            return new AuthenticationResponseDTO(accessToken, refreshToken);
        } catch (JwtException | UsernameNotFoundException e) {
            throw new JwtAuthenticationException("Invalid refresh JWT token", e);
        }
    }


    @Override
    public User register(RegistrationRequestDTO request) {
        if (userService.existsByEmail(request.getUsername())) {
            throw new RegistrationException(HttpStatus.CONFLICT, "User with username " + request.getUsername() + " already exists");
        }

        ClientDetails clientDetails = ClientDetails.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .middleName(request.getMiddleName())
                .phoneNumber(request.getPhoneNumber())
                .build();

        User user = User.builder()
                .email(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .status(UserStatus.ACTIVE)
                .clientDetails(clientDetails)
                .build();

        User registeredUser = userService.save(user);

        log.info("New user has been successfully registered: {}", registeredUser);
        return registeredUser;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userService.findByEmail(username).orElseThrow(() ->
                new UsernameNotFoundException("User with username " + username + " not found"));
    }
}

