package ru.qwonix.foxwhiskersapi.rest;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.qwonix.foxwhiskersapi.TestcontainersConfiguration;
import ru.qwonix.foxwhiskersapi.entity.Permission;
import ru.qwonix.foxwhiskersapi.entity.Role;
import ru.qwonix.foxwhiskersapi.entity.User;
import ru.qwonix.foxwhiskersapi.entity.UserStatus;
import ru.qwonix.foxwhiskersapi.repository.AuthenticationRepository;
import ru.qwonix.foxwhiskersapi.security.Token;
import ru.qwonix.foxwhiskersapi.service.AuthenticationService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Sql("/sql/authentication_rest_controller/test_data.sql")
@Transactional
@SpringBootTest(classes = TestcontainersConfiguration.class)
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthenticationControllerIT {
    public static final String PHONE_NUMBER = "+7 (987) 654 32-10";
    private static final User MARIA_SIDOROVA_2 = new User(
            UUID.fromString("e2713f8b-7f91-4b32-aead-45a0453c3d3d"),
            PHONE_NUMBER,
            "maria_sidorova@example.com",
            "Мария",
            "Сидорова",
            "Ивановна",
            UserStatus.ACTIVE,
            Role.CLIENT,
            LocalDateTime.of(2023, 8, 18, 9, 15),
            LocalDateTime.of(2023, 9, 5, 14, 45)
    );

    public static final String CODE = "1111";

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AuthenticationRepository authenticationRepository;

    @Autowired
    AuthenticationService authenticationService;
    private String serializedRefreshToken;

    @BeforeAll
    void setUpAll() {
        authenticationRepository.add(PHONE_NUMBER, CODE, Duration.ofSeconds(10));
    }

    @BeforeEach
    void setUp() {
        this.serializedRefreshToken = authenticationService.serializeRefreshToken(new Token(PHONE_NUMBER, List.of(Permission.TOKEN_REFRESH)));
    }

    @Test
    void handleSendVerificationCode_ValidPhoneNumber_ReturnsSuccessStatus() throws Exception {
        var requestBuilder = post("/api/v1/auth/code")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "phoneNumber": "+7 (987) 654 32-10"
                        }
                        """
                );

        this.mockMvc.perform(requestBuilder)
                .andExpectAll(status().isOk());
    }


    @Test
    void handleAuthenticateByCode_ValidCode_ReturnsAuthenticationTokens() throws Exception {
        var requestBuilder = post("/api/v1/auth")
                .header(HttpHeaders.AUTHORIZATION, "CodeVerification " + Base64.getEncoder().encodeToString((PHONE_NUMBER + ':' + CODE).getBytes()));
        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.accessToken").exists(),
                        jsonPath("$.refreshToken").exists()
                );
    }

    @Test
    void handleAuthenticateByCode_InvalidCode_ReturnsValidErrorResponse() throws Exception {
        final var INVALID_CODE = "1112";
        var requestBuilder = post("/api/v1/auth")
                .header(HttpHeaders.AUTHORIZATION,
                        "CodeVerification " + Base64.getEncoder().encodeToString((PHONE_NUMBER + ':' + INVALID_CODE).getBytes()));
        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isUnauthorized()
                );
    }

    @Test
    void handleRefreshTokens_ValidRefreshToken_ReturnsNewAuthenticationTokens() throws Exception {
        var requestBuilder = post("/api/v1/auth/refresh")
                .header(HttpHeaders.AUTHORIZATION,
                        "Bearer " + serializedRefreshToken);
        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.accessToken").exists(),
                        jsonPath("$.refreshToken").exists()
                );
    }

    @Test
    void handleRefreshTokens_ExpiredRefreshToken_ReturnsValidErrorResponse() throws Exception {
        var oldRefreshToken = serializedRefreshToken;
        var newRefreshToken = authenticationService.serializeRefreshToken(new Token(PHONE_NUMBER, Role.CLIENT.getAuthorities()));

        var requestBuilder = post("/api/v1/auth/refresh")
                .header(HttpHeaders.AUTHORIZATION,
                        "Bearer " + oldRefreshToken);

        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isUnauthorized()
                );
    }

}