package ru.qwonix.foxwhiskersapi.security.converter;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import ru.qwonix.foxwhiskersapi.service.AuthenticationService;

import java.util.Base64;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationRequestConverterTest {

    private static final String PHONE_NUMBER = "+7 (999) 123-45-67";
    private static final String CODE = "1111";
    private static final String ENCODED_VALID_TOKEN = Base64.getEncoder().encodeToString(
            (PHONE_NUMBER + CodeVerificationAuthenticationConverter.TOKEN_SEPARATOR + CODE).getBytes()
    );

    @Mock
    AuthenticationService authenticationService;
    @Mock
    HttpServletRequest request;

    @InjectMocks
    JwtAuthenticationRequestConverter jwtAuthenticationRequestConverter;


    @Test
    void convert_ValidData_ReturnsAuthentication() {
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + ENCODED_VALID_TOKEN);
        when(authenticationService.verifyAuthenticationCode(PHONE_NUMBER, CODE)).thenReturn(true);
//
//        var authentication = converter.convert(request);
//
//        verify(authenticationService).verifyCodeAuthentication(PHONE_NUMBER, CODE);
//        assertNotNull(authentication);
//        assertInstanceOf(PreAuthenticatedAuthenticationToken.class, authentication);
//        assertEquals(PHONE_NUMBER, authentication.getPrincipal().toString());
//        assertEquals(CODE, authentication.getCredentials().toString());
//        assertFalse(authentication.isAuthenticated());
    }

}