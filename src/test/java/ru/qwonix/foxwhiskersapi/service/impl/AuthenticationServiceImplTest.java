package ru.qwonix.foxwhiskersapi.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.qwonix.foxwhiskersapi.security.JwtAuthenticationProvider;
import ru.qwonix.foxwhiskersapi.service.ClientService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {

    @Mock
    ClientService clientService;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    AuthenticationManager authenticationManager;
    @Mock
    JwtAuthenticationProvider jwtAuthenticationProvider;

    @InjectMocks
    AuthenticationServiceImpl authenticationService;

//    @Test
//    void loadUserByUsername_UsernameExists_ReturnsValidUserDetails() {
//        String username = "email@example.com";
//        UserDetails userDetails = new Client(1L, username, "password", Role.USER, UserStatus.ACTIVE, null);
//        doReturn(Optional.of(userDetails)).when(userService).findByPhoneNumber(username);
//
//        UserDetails actual = authenticationService.loadUserByUsername(username);
//
//        assertNotNull(actual);
//        assertNotNull(actual);
//        assertEquals(userDetails, actual);
//    }


    @Test
    void loadUserByUsername_UsernameNotExists_ThrowsUsernameNotFoundException() {
        String username = "email@example.com";
        doReturn(Optional.empty()).when(clientService).findByPhoneNumber(username);

        assertThrows(UsernameNotFoundException.class, () -> authenticationService.loadUserByUsername(username));
    }
}