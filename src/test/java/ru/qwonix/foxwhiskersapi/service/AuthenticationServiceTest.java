package ru.qwonix.foxwhiskersapi.service;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import io.jsonwebtoken.security.WeakKeyException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.qwonix.foxwhiskersapi.entity.Client;
import ru.qwonix.foxwhiskersapi.repository.AuthenticationRepository;
import ru.qwonix.foxwhiskersapi.service.impl.JwtAuthenticationService;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    private static final List<String> PERMISSIONS = List.of();
    private static final String PHONE_NUMBER = "+7 (999) 123-45-67";
    public static final String CODE = "0000";
    @Mock
    ClientService clientService;

    @Mock
    AuthenticationRepository authenticationRepository;

    AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        var accessJwtKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        var refreshJwtKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        authenticationService = new JwtAuthenticationService(clientService, authenticationRepository, accessJwtKey, refreshJwtKey);
    }

    @Test
    void constructor_SecretTokensAreNull_ThrowsIllegalArgumentException() {
        final String accessJwtSecret = null;
        final String refreshJwtSecret = null;

        assertThrows(IllegalArgumentException.class, () ->
                new JwtAuthenticationService(clientService, authenticationRepository, accessJwtSecret, refreshJwtSecret));

    }

    @Test
    void constructor_SecretTokensAreWeak_ThrowsWeakKeyException() {
        final var accessJwtSecret = "h9dHOdBnBQ2AVk5dX8wV3zXoxBwnlh";
        final var refreshJwtSecret = "ifEPLE6seg6O6dGeuFwiaasdfiuh23f";

        assertThrows(WeakKeyException.class, () ->
                new JwtAuthenticationService(clientService, authenticationRepository, accessJwtSecret, refreshJwtSecret));

    }


    @Test
    void verifyCodeAuthentication_DataIsValid_VerificationIsSuccess() {
        doReturn(true).when(authenticationRepository).hasKeyAndValue(PHONE_NUMBER, CODE);

        boolean isVerified = authenticationService.verifyCodeAuthentication(PHONE_NUMBER, CODE);

        assertTrue(isVerified);
        verify(authenticationRepository).hasKeyAndValue(PHONE_NUMBER, CODE);
    }

    @Test
    void verifyCodeAuthentication_DataIsInvalid_VerificationIsSuccess() {
        final String INVALID_CODE = "0001";
        doReturn(false).when(authenticationRepository).hasKeyAndValue(PHONE_NUMBER, INVALID_CODE);

        boolean isVerified = authenticationService.verifyCodeAuthentication(PHONE_NUMBER, INVALID_CODE);

        assertFalse(isVerified);
        verify(authenticationRepository).hasKeyAndValue(PHONE_NUMBER, INVALID_CODE);
    }

    @Test
    void sendCode_ClientAlreadyExists_SuccessGeneration() {
        doReturn(true).when(clientService).exists(PHONE_NUMBER);

        authenticationService.sendCode(PHONE_NUMBER);

        verify(authenticationRepository).add(eq(PHONE_NUMBER), anyString(), any(Duration.class));
    }

    @Test
    void sendCode_ClientNotExists_SuccessGeneration() {
        doReturn(false).when(clientService).exists(PHONE_NUMBER);

        authenticationService.sendCode(PHONE_NUMBER);

        var clientArgumentCaptor = ArgumentCaptor.forClass(Client.class);
        verify(clientService).save(clientArgumentCaptor.capture());
        var student = clientArgumentCaptor.getValue();
        assertEquals(PHONE_NUMBER, student.getPhoneNumber());
        verify(authenticationRepository).add(eq(PHONE_NUMBER), anyString(), any(Duration.class));
    }

    @Test
    void generateAccessToken_SubjectIsValid_ReturnValidToken() {
        var token = authenticationService.generateAccessToken(PHONE_NUMBER, PERMISSIONS);

        var claims = assertDoesNotThrow(() ->
                authenticationService.getAccessClaims(token));
        assertEquals(PHONE_NUMBER, claims.getSubject());
        assertEquals(PERMISSIONS, claims.get(authenticationService.PERMISSIONS_CLAIM));
    }

    @Test
    void generateAccessToken_SubjectIsNull_ReturnValidToken() {
        var token = authenticationService.generateAccessToken(null, PERMISSIONS);

        var claims = assertDoesNotThrow(() ->
                authenticationService.getAccessClaims(token));
        assertNull(claims.getSubject());
        assertEquals(PERMISSIONS, claims.get(authenticationService.PERMISSIONS_CLAIM));
    }

    @Test
    void generateRefreshToken_SubjectIsValid_ReturnValidToken() {
        var token = authenticationService.generateRefreshToken(PHONE_NUMBER);

        var claims = assertDoesNotThrow(() ->
                authenticationService.getRefreshClaims(token));
        assertEquals(PHONE_NUMBER, claims.getSubject());
    }

    @Test
    void generateRefreshToken_SubjectIsNull_ReturnValidToken() {
        var token = authenticationService.generateRefreshToken(null);

        var claims = assertDoesNotThrow(() ->
                authenticationService.getRefreshClaims(token));
        assertNull(claims.getSubject());
    }


    @Test
    void getAccessClaims_KeyIsInvalid_ThrowsMalformedJwtException() {
        Executable executable = () -> authenticationService.getAccessClaims("not a token");

        assertThrows(MalformedJwtException.class, executable);
    }

    @Test
    void getAccessClaims_KeyIsInvalid_ThrowsSignatureException() {
        var anotherAccessJwtKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        var anotherRefreshJwtKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        var anotherAuthenticationService = new JwtAuthenticationService(clientService, authenticationRepository,
                anotherAccessJwtKey,
                anotherRefreshJwtKey);
        String token = authenticationService.generateAccessToken(PHONE_NUMBER, PERMISSIONS);

        Executable executable = () ->
                anotherAuthenticationService.getAccessClaims(token);

        assertThrows(SignatureException.class, executable);
    }

    @Test
    void getAccessClaims_ExpirationIsZero_ThrowsExpiredJwtException() {
        authenticationService.setAccessTokenTtl(Duration.ZERO);
        String token = authenticationService.generateAccessToken(PHONE_NUMBER, PERMISSIONS);

        Executable executable = () ->
                authenticationService.getAccessClaims(token);

        assertThrows(ExpiredJwtException.class, executable);
    }

    @Test
    void getAccessClaims_TokenIsEmpty_ThrowsIllegalArgumentException() {
        Executable executable = () ->
                authenticationService.getAccessClaims(null);

        assertThrows(IllegalArgumentException.class, executable);
    }

    @Test
    void getRefreshClaims_KeyIsInvalid_ThrowsMalformedJwtException() {
        Executable executable = () ->
                authenticationService.getRefreshClaims("not a token");

        assertThrows(MalformedJwtException.class, executable);
    }

    @Test
    void getRefreshClaims_KeyIsInvalid_ThrowsSignatureException() {
        var anotherAccessJwtKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        var anotherRefreshJwtKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        var anotherAuthenticationService = new JwtAuthenticationService(clientService, authenticationRepository,
                anotherAccessJwtKey,
                anotherRefreshJwtKey);
        String token = anotherAuthenticationService.generateRefreshToken(PHONE_NUMBER);

        Executable executable = () ->
                authenticationService.getRefreshClaims(token);

        assertThrows(SignatureException.class, executable);
    }

    @Test
    void getRefreshClaims_ExpirationIsZero_ThrowsExpiredJwtException() {
        authenticationService.setRefreshTokenTtl(Duration.ZERO);
        String token = authenticationService.generateRefreshToken(PHONE_NUMBER);

        Executable executable = () ->
                authenticationService.getRefreshClaims(token);

        assertThrows(ExpiredJwtException.class, executable);
    }

    @Test
    void getRefreshClaims_TokenIsEmpty_ThrowsIllegalArgumentException() {
        Executable executable = () ->
                authenticationService.getRefreshClaims(null);

        assertThrows(IllegalArgumentException.class, executable);
    }

}