package ru.qwonix.foxwhiskersapi.security.converter;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import ru.qwonix.foxwhiskersapi.exception.InvalidTokenFormatException;
import ru.qwonix.foxwhiskersapi.service.AuthenticationService;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CodeVerificationConverterTest {

    private static final String PHONE_NUMBER = "+7 (999) 123-45-67";
    private static final String CODE = "1111";
    private static final String ENCODED_VALID_TOKEN = Base64.getEncoder().encodeToString(
            (PHONE_NUMBER + CodeVerificationConverter.TOKEN_SEPARATOR + CODE).getBytes()
    );

    @Mock
    HttpServletRequest request;

    @Mock
    AuthenticationService authenticationService;

    @InjectMocks
    CodeVerificationConverter converter;

    @Test
    void convert_ValidData_ReturnsAuthentication() {
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("CodeVerification " + ENCODED_VALID_TOKEN);
        when(authenticationService.verifyAuthenticationCode(PHONE_NUMBER, CODE)).thenReturn(true);

        var authentication = converter.convert(request);

        verify(authenticationService).verifyAuthenticationCode(PHONE_NUMBER, CODE);
        assertNotNull(authentication);
        assertInstanceOf(PreAuthenticatedAuthenticationToken.class, authentication);
        assertEquals(PHONE_NUMBER, authentication.getPrincipal().toString());
        assertEquals(CODE, authentication.getCredentials().toString());
        assertFalse(authentication.isAuthenticated());
    }

    @Test
    void convert_InvalidData_ThrowsBadCredentialsException() {
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("CodeVerification " + ENCODED_VALID_TOKEN);
        when(authenticationService.verifyAuthenticationCode(PHONE_NUMBER, CODE)).thenReturn(false);

        Executable executable = () -> converter.convert(request);

        assertThrows(BadCredentialsException.class, executable);
    }

    @Test
    void convert_RequestDoesNotContainsAuthorizationHeader_ReturnsNull() {
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);

        var authentication = converter.convert(request);

        assertNull(authentication);
    }

    @Test
    void convert_InvalidAuthenticationScheme_ReturnsNull() {
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + ENCODED_VALID_TOKEN);

        var authentication = converter.convert(request);

        assertNull(authentication);
    }

    @Test
    void convert_InvalidTokenEncoding_ThrowsInvalidTokenFormatException() {
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("CodeVerification " + "not a base64 string");

        Executable executable = () -> converter.convert(request);

        assertThrows(InvalidTokenFormatException.class, executable);
    }

    @Test
    void convert_InvalidTokenSeparator_ThrowsInvalidTokenFormatException() {
        final var INVALID_TOKEN_SEPARATOR = '*';
        when(request.getHeader(HttpHeaders.AUTHORIZATION))
                .thenReturn("CodeVerification " + Base64.getEncoder().encodeToString((PHONE_NUMBER + INVALID_TOKEN_SEPARATOR + CODE).getBytes()));

        Executable executable = () -> converter.convert(request);

        assertThrows(InvalidTokenFormatException.class, executable);
    }
}