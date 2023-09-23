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
import ru.qwonix.foxwhiskersapi.entity.User;
import ru.qwonix.foxwhiskersapi.repository.AuthenticationRepository;
import ru.qwonix.foxwhiskersapi.service.impl.JwtAuthenticationService;

import java.security.Key;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    public static final String CODE = "0000";
    private static final List<String> PERMISSIONS = List.of();
    private static final String PHONE_NUMBER = "+7 (999) 123-45-67";
    @Mock
    UserService userService;

    @Mock
    AuthenticationRepository authenticationRepository;

    AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        var accessJwtKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        var refreshJwtKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        authenticationService = new JwtAuthenticationService(userService, authenticationRepository, accessJwtKey, refreshJwtKey);
    }

    @Test
    void constructor_SecretTokensAreNull_ThrowsIllegalArgumentException() {
        final Key accessJwtSecret = null;
        final Key refreshJwtSecret = null;

        assertThrows(IllegalArgumentException.class, () ->
                new JwtAuthenticationService(userService, authenticationRepository, accessJwtSecret, refreshJwtSecret));

    }

    @Test
    void verifyCodeAuthentication_DataIsValid_VerificationIsSuccess() {
        doReturn(true).when(authenticationRepository).hasKeyAndValue(PHONE_NUMBER, CODE);

        boolean isVerified = authenticationService.verifyAuthenticationCode(PHONE_NUMBER, CODE);

        assertTrue(isVerified);
        verify(authenticationRepository).hasKeyAndValue(PHONE_NUMBER, CODE);
    }

    @Test
    void verifyCodeAuthentication_DataIsInvalid_VerificationIsSuccess() {
        final String INVALID_CODE = "0001";
        doReturn(false).when(authenticationRepository).hasKeyAndValue(PHONE_NUMBER, INVALID_CODE);

        boolean isVerified = authenticationService.verifyAuthenticationCode(PHONE_NUMBER, INVALID_CODE);

        assertFalse(isVerified);
        verify(authenticationRepository).hasKeyAndValue(PHONE_NUMBER, INVALID_CODE);
    }

    @Test
    void sendCode_PhoneNumberAlreadyExists_SuccessGeneration() {
        doReturn(true).when(userService).exists(PHONE_NUMBER);

        authenticationService.createAuthenticationCode(PHONE_NUMBER);

        verify(authenticationRepository).add(eq(PHONE_NUMBER), anyString(), any(Duration.class));
    }

    @Test
    void sendCode_PhoneNumberNotExists_SuccessGeneration() {
        doReturn(false).when(userService).exists(PHONE_NUMBER);

        authenticationService.createAuthenticationCode(PHONE_NUMBER);

        var userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userService).save(userArgumentCaptor.capture());
        var student = userArgumentCaptor.getValue();
        assertEquals(PHONE_NUMBER, student.getPhoneNumber());
        verify(authenticationRepository).add(eq(PHONE_NUMBER), anyString(), any(Duration.class));
    }
//
//    @Test
//    void generateAccessToken_SubjectIsValid_ReturnValidToken() {
//        var token = authenticationService.generateAccessToken(PHONE_NUMBER, PERMISSIONS);
//
//        var claims = assertDoesNotThrow(() ->
//                authenticationService.getAccessToken(token));
//        assertEquals(PHONE_NUMBER, claims.getSubject());
//        assertEquals(PERMISSIONS, claims.get(authenticationService.PERMISSIONS_CLAIM));
//    }
//
//    @Test
//    void generateAccessToken_SubjectIsNull_ReturnValidToken() {
//        var token = authenticationService.generateAccessToken(null, PERMISSIONS);
//
//        var claims = assertDoesNotThrow(() ->
//                authenticationService.getAccessToken(token));
//        assertNull(claims.getSubject());
//        assertEquals(PERMISSIONS, claims.get(authenticationService.PERMISSIONS_CLAIM));
//    }
//
//    @Test
//    void generateRefreshToken_SubjectIsValid_ReturnValidToken() {
//        var token = authenticationService.generateRefreshToken(PHONE_NUMBER);
//
//        var claims = assertDoesNotThrow(() ->
//                authenticationService.getRefreshToken(token));
//        assertEquals(PHONE_NUMBER, claims.getSubject());
//    }
//
//    @Test
//    void generateRefreshToken_SubjectIsNull_ReturnValidToken() {
//        var token = authenticationService.generateRefreshToken(null);
//
//        var claims = assertDoesNotThrow(() ->
//                authenticationService.getRefreshToken(token));
//        assertNull(claims.getSubject());
//    }
//
//
//    @Test
//    void getAccessClaims_KeyIsInvalid_ThrowsMalformedJwtException() {
//        Executable executable = () -> authenticationService.getAccessToken("not a token");
//
//        assertThrows(MalformedJwtException.class, executable);
//    }
//
//    @Test
//    void getAccessClaims_KeyIsInvalid_ThrowsSignatureException() {
//        var anotherAccessJwtKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
//        var anotherRefreshJwtKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
//        var anotherAuthenticationService = new JwtAuthenticationService(userService, authenticationRepository,
//                anotherAccessJwtKey,
//                anotherRefreshJwtKey);
//        String token = authenticationService.generateAccessToken(PHONE_NUMBER, PERMISSIONS);
//
//        Executable executable = () ->
//                anotherAuthenticationService.getAccessToken(token);
//
//        assertThrows(SignatureException.class, executable);
//    }
//
//    @Test
//    void getAccessClaims_ExpirationIsZero_ThrowsExpiredJwtException() {
//        authenticationService.setAccessTokenTtl(Duration.ZERO);
//        String token = authenticationService.generateAccessToken(PHONE_NUMBER, PERMISSIONS);
//
//        Executable executable = () ->
//                authenticationService.getAccessToken(token);
//
//        assertThrows(ExpiredJwtException.class, executable);
//    }

    @Test
    void getAccessClaims_TokenIsEmpty_ThrowsIllegalArgumentException() {
        Executable executable = () ->
                authenticationService.parseAccessToken(null);

        assertThrows(IllegalArgumentException.class, executable);
    }

    @Test
    void getRefreshClaims_KeyIsInvalid_ThrowsMalformedJwtException() {
        Executable executable = () ->
                authenticationService.parseRefreshToken("not a token");

        assertThrows(MalformedJwtException.class, executable);
    }

//    @Test
//    void getRefreshClaims_KeyIsInvalid_ThrowsSignatureException() {
//        var anotherAccessJwtKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
//        var anotherRefreshJwtKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
//        var anotherAuthenticationService = new JwtAuthenticationService(userService, authenticationRepository,
//                anotherAccessJwtKey,
//                anotherRefreshJwtKey);
//        String token = anotherAuthenticationService.generateRefreshToken(PHONE_NUMBER);
//
//        Executable executable = () ->
//                authenticationService.parseRefreshToken(token);
//
//        assertThrows(SignatureException.class, executable);
//    }
//
//    @Test
//    void getRefreshClaims_ExpirationIsZero_ThrowsExpiredJwtException() {
//        authenticationService.setRefreshTokenTtl(Duration.ZERO);
//        String token = authenticationService.generateRefreshToken(PHONE_NUMBER);
//
//        Executable executable = () ->
//                authenticationService.parseRefreshToken(token);
//
//        assertThrows(ExpiredJwtException.class, executable);
//    }

    @Test
    void getRefreshClaims_TokenIsEmpty_ThrowsIllegalArgumentException() {
        Executable executable = () ->
                authenticationService.parseRefreshToken(null);

        assertThrows(IllegalArgumentException.class, executable);
    }

}