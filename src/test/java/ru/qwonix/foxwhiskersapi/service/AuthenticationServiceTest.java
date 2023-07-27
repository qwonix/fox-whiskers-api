package ru.qwonix.foxwhiskersapi.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import ru.qwonix.foxwhiskersapi.dto.ClientAuthenticationRequestDTO;
import ru.qwonix.foxwhiskersapi.dto.RefreshJwtRequestDTO;
import ru.qwonix.foxwhiskersapi.entity.Client;
import ru.qwonix.foxwhiskersapi.exception.JwtAuthenticationException;
import ru.qwonix.foxwhiskersapi.repository.AuthenticationRepository;
import ru.qwonix.foxwhiskersapi.security.CodeAuthentication;
import ru.qwonix.foxwhiskersapi.service.impl.AuthenticationServiceImpl;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    private static final String PHONE_NUMBER = "+7 (999) 123-45-67";
    private static final int CODE = 1234;
    private static final String ACCESS_TOKEN = "access-token";
    private static final String REFRESH_TOKEN = "refresh-token";

    @Mock
    ClientService clientService;

    @Mock
    AuthenticationRepository authenticationRepository;

    @InjectMocks
    AuthenticationServiceImpl authenticationService;

    @Test
    void authenticate_AuthenticationDataIsValid_ReturnsValidTokens() {
        doReturn(CodeAuthentication.authenticated(PHONE_NUMBER)).when(this.authenticationRepository).authenticate(PHONE_NUMBER, CODE);
        doReturn(ACCESS_TOKEN).when(this.authenticationRepository).generateAccessToken(PHONE_NUMBER);
        doReturn(REFRESH_TOKEN).when(this.authenticationRepository).generateRefreshToken(PHONE_NUMBER);

        var responseDTO = authenticationService.authenticate(new ClientAuthenticationRequestDTO(PHONE_NUMBER, CODE));

        assertEquals(ACCESS_TOKEN, responseDTO.jwtAccessToken());
        assertEquals(REFRESH_TOKEN, responseDTO.jwtRefreshToken());
    }

    @Test
    void authenticate_AuthenticationDataIsInvalid_ThrowsBadCredentialsException() {
        doReturn(CodeAuthentication.unauthenticated()).when(this.authenticationRepository).authenticate(PHONE_NUMBER, CODE);

        Executable authenticate = () -> authenticationService.authenticate(new ClientAuthenticationRequestDTO(PHONE_NUMBER, CODE));

        assertThrows(BadCredentialsException.class, authenticate);
    }


    @Test
    void sendCode_PhoneNumberIsNew_VerifyUserInsertAndCodeSend() {
        doReturn(false).when(this.clientService).exists(PHONE_NUMBER);
        var clientArgumentCaptor = ArgumentCaptor.forClass(Client.class);
        final var client = new Client(PHONE_NUMBER);

        authenticationService.sendCode(PHONE_NUMBER);

        verify(this.clientService).save(clientArgumentCaptor.capture());
        var captoredClient = clientArgumentCaptor.getValue();
        assertThat(captoredClient).isEqualTo(client);
        verify(this.authenticationRepository).sendCode(PHONE_NUMBER);
    }

    @Test
    void sendCode_PhoneNumberAlreadyExists_VerifyCodeSend() {
        doReturn(true).when(this.clientService).exists(PHONE_NUMBER);

        authenticationService.sendCode(PHONE_NUMBER);

        verify(this.authenticationRepository).sendCode(PHONE_NUMBER);
    }

    @Test
    void refreshTokens_RefreshTokenIsValid_ReturnsNewTokens() {
        doReturn(PHONE_NUMBER).when(this.authenticationRepository).getSubjectFromTokenClaims(REFRESH_TOKEN);
        doReturn(true).when(this.authenticationRepository).revokeRefreshToken(REFRESH_TOKEN);
        final String NEW_ACCESS_TOKEN = "new-access-token";
        final String NEW_REFRESH_TOKEN = "new-refresh-token";
        doReturn(NEW_ACCESS_TOKEN).when(this.authenticationRepository).generateAccessToken(PHONE_NUMBER);
        doReturn(NEW_REFRESH_TOKEN).when(this.authenticationRepository).generateRefreshToken(PHONE_NUMBER);

        var responseDTO = authenticationService.refreshTokens(new RefreshJwtRequestDTO(REFRESH_TOKEN));

        assertEquals(NEW_ACCESS_TOKEN, responseDTO.jwtAccessToken());
        assertEquals(NEW_REFRESH_TOKEN, responseDTO.jwtRefreshToken());
        verify(this.authenticationRepository).revokeRefreshToken(REFRESH_TOKEN);
    }

    @Test
    void refreshTokens_RefreshTokenIsInValid_ThrowsJwtAuthenticationException() {
        doThrow(JwtAuthenticationException.class).when(this.authenticationRepository).getSubjectFromTokenClaims(REFRESH_TOKEN);

        Executable authenticate = () -> authenticationService.refreshTokens(new RefreshJwtRequestDTO(REFRESH_TOKEN));

        assertThrows(JwtAuthenticationException.class, authenticate);
    }

    @Test
    void refreshTokens_RevokeTokenFailure_ThrowsJwtAuthenticationException() {
        doReturn(PHONE_NUMBER).when(this.authenticationRepository).getSubjectFromTokenClaims(REFRESH_TOKEN);
        doReturn(false).when(this.authenticationRepository).revokeRefreshToken(REFRESH_TOKEN);

        Executable authenticate = () -> authenticationService.refreshTokens(new RefreshJwtRequestDTO(REFRESH_TOKEN));

        assertThrows(JwtAuthenticationException.class, authenticate);
    }


}