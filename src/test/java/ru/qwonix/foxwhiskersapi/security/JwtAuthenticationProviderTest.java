package ru.qwonix.foxwhiskersapi.security;


import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import ru.qwonix.foxwhiskersapi.entity.Role;
import ru.qwonix.foxwhiskersapi.exception.JwtAuthenticationException;

import io.jsonwebtoken.security.SignatureException;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class JwtAuthenticationProviderTest {
    private static final String SUBJECT = "SUBJECT STRING";

    JwtAuthenticationProvider jwtAuthenticationProvider = new JwtAuthenticationProvider(Duration.ofSeconds(2), Duration.ofSeconds(2));

    @Test
    void supports_JwtAuthenticationToken_IsTrue() {
        boolean supports = jwtAuthenticationProvider.supports(JwtAuthenticationToken.class);

        assertTrue(supports);
    }

    @Test
    void supports_JwtAuthenticationToken_IsFalse() {
        boolean supports = jwtAuthenticationProvider.supports(AbstractAuthenticationToken.class);

        assertFalse(supports);
    }

    @Test
    void authenticate_TokenIsValid_ReturnSuccessAuthentication() {
        var token = jwtAuthenticationProvider.generateAccessToken(SUBJECT, Role.CLIENT.name());
        var authentication = new JwtAuthenticationToken(token);

        var actualAuthentication = jwtAuthenticationProvider.authenticate(authentication);

        assertTrue(actualAuthentication.isAuthenticated());
        assertEquals(SUBJECT, actualAuthentication.getPrincipal());
        var expectedClientAuthorities = Role.CLIENT.getAuthorities();
        var actualClientAuthorities = actualAuthentication.getAuthorities();
        assertEquals(expectedClientAuthorities.size(), actualClientAuthorities.size());
        assertTrue(actualClientAuthorities.containsAll(expectedClientAuthorities));
    }

    @Test
    void authenticate_TokenIsExpired_ThrowsJwtAuthenticationException() {
        jwtAuthenticationProvider.setAccessExpiration(Duration.ZERO);
        var token = jwtAuthenticationProvider.generateAccessToken(SUBJECT, Role.CLIENT.name());
        var authentication = new JwtAuthenticationToken(token);

        Executable executable = () -> jwtAuthenticationProvider.authenticate(authentication);

        assertThrows(JwtAuthenticationException.class, executable);
    }

    @Test
    void authenticate_TokenIsInvalid_ThrowsJwtAuthenticationException() {
        var anotherAuthenticationProvider = new JwtAuthenticationProvider();
        var token = anotherAuthenticationProvider.generateAccessToken(SUBJECT, Role.CLIENT.name());
        var authentication = new JwtAuthenticationToken(token);

        Executable executable = () -> jwtAuthenticationProvider.authenticate(authentication);

        assertThrows(JwtAuthenticationException.class, executable);
    }

    @Test
    void generateAccessToken_SubjectIsValid_ReturnValidToken() {
        var token = jwtAuthenticationProvider.generateAccessToken(SUBJECT, Role.CLIENT.name());

        var body = assertDoesNotThrow(() ->
                jwtAuthenticationProvider.getAccessClaims(token));

        assertEquals(SUBJECT, body.getSubject());
        assertEquals(Role.CLIENT.name(), body.get(JwtAuthenticationProvider.ROLE_CLAIM));
    }

    @Test
    void generateAccessToken_SubjectIsNull_ReturnValidToken() {
        var token = jwtAuthenticationProvider.generateAccessToken(null, Role.CLIENT.name());

        var body = assertDoesNotThrow(() ->
                jwtAuthenticationProvider.getAccessClaims(token));

        assertNull(body.getSubject());
        assertEquals(Role.CLIENT.name(), body.get(JwtAuthenticationProvider.ROLE_CLAIM));
    }

    @Test
    void generateRefreshToken_SubjectIsValid_ReturnValidToken() {
        var token = jwtAuthenticationProvider.generateRefreshToken(SUBJECT);

        var body = assertDoesNotThrow(() ->
                jwtAuthenticationProvider.getRefreshClaims(token));

        assertEquals(SUBJECT, body.getSubject());
    }

    @Test
    void generateRefreshToken_SubjectIsNull_ReturnValidToken() {
        var token = jwtAuthenticationProvider.generateRefreshToken(null);

        var body = assertDoesNotThrow(() ->
                jwtAuthenticationProvider.getRefreshClaims(token));

        assertNull(body.getSubject());
    }


    @Test
    void getAccessClaims_KeyIsInvalid_ThrowsMalformedJwtException() {
        Executable executable = () ->
                jwtAuthenticationProvider.getAccessClaims("not a token");

        assertThrows(MalformedJwtException.class, executable);
    }

    @Test
    void getAccessClaims_KeyIsInvalid_ThrowsSignatureException() {
        var anotherAuthenticationProvider = new JwtAuthenticationProvider();
        String token = jwtAuthenticationProvider.generateAccessToken(SUBJECT, Role.CLIENT.name());

        Executable executable = () ->
                anotherAuthenticationProvider.getAccessClaims(token);

        assertThrows(SignatureException.class, executable);
    }

    @Test
    void getAccessClaims_ExpirationIsZero_ThrowsExpiredJwtException() {
        jwtAuthenticationProvider.setAccessExpiration(Duration.ZERO);
        String token = jwtAuthenticationProvider.generateAccessToken(SUBJECT, Role.CLIENT.name());

        Executable executable = () ->
                jwtAuthenticationProvider.getAccessClaims(token);

        assertThrows(ExpiredJwtException.class, executable);
    }

    @Test
    void getAccessClaims_TokenIsEmpty_ThrowsIllegalArgumentException() {
        Executable executable = () ->
                jwtAuthenticationProvider.getAccessClaims(Strings.EMPTY);

        assertThrows(IllegalArgumentException.class, executable);
    }

    @Test
    void getRefreshClaims_KeyIsInvalid_ThrowsMalformedJwtException() {
        Executable executable = () ->
                jwtAuthenticationProvider.getRefreshClaims("not a token");

        assertThrows(MalformedJwtException.class, executable);
    }

    @Test
    void getRefreshClaims_KeyIsInvalid_ThrowsSignatureException() {
        var anotherAuthenticationProvider = new JwtAuthenticationProvider();
        String token = anotherAuthenticationProvider.generateRefreshToken(SUBJECT);

        Executable executable = () ->
                jwtAuthenticationProvider.getRefreshClaims(token);

        assertThrows(SignatureException.class, executable);
    }

    @Test
    void getRefreshClaims_ExpirationIsZero_ThrowsExpiredJwtException() {
        jwtAuthenticationProvider.setRefreshExpiration(Duration.ZERO);
        String token = jwtAuthenticationProvider.generateRefreshToken(SUBJECT);

        Executable executable = () ->
                jwtAuthenticationProvider.getRefreshClaims(token);

        assertThrows(ExpiredJwtException.class, executable);
    }
    @Test
    void getRefreshClaims_TokenIsEmpty_ThrowsIllegalArgumentException() {
        Executable executable = () ->
                jwtAuthenticationProvider.getRefreshClaims(Strings.EMPTY);

        assertThrows(IllegalArgumentException.class, executable);
    }

}