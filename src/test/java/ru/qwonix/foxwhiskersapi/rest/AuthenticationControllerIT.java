package ru.qwonix.foxwhiskersapi.rest;

import org.junit.jupiter.api.BeforeAll;
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
import ru.qwonix.foxwhiskersapi.entity.Role;
import ru.qwonix.foxwhiskersapi.entity.User;
import ru.qwonix.foxwhiskersapi.entity.UserStatus;
import ru.qwonix.foxwhiskersapi.repository.AuthenticationRepository;
import ru.qwonix.foxwhiskersapi.service.AuthenticationService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Sql("/sql/authentication_rest_controller/test_data.sql")
@Transactional
@SpringBootTest(classes = TestcontainersConfiguration.class)
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthenticationControllerIT {
    private static final User MARIA_SIDOROVA = new User(
            UUID.fromString("e2713f8b-7f91-4b32-aead-45a0453c3d3d"),
            "+7 (987) 654 32-10",
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

    @BeforeAll
    public void setUp() {
        authenticationRepository.add(MARIA_SIDOROVA.getPhoneNumber(), CODE, Duration.ofSeconds(10));
    }


    @Test
    void handleSendVerificationCode_ReturnsSuccessStatus() throws Exception {
        var requestBuilder = post("/api/v1/auth/code")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "phoneNumber": "%s"
                        }
                        """.formatted(MARIA_SIDOROVA.getPhoneNumber()));

        this.mockMvc.perform(requestBuilder)
                .andExpectAll(status().isOk());
    }


    @Test
    void handleAuthenticateByCode_DataIsValid_ReturnsTokens() throws Exception {
        var requestBuilder = post("/api/v1/auth")
                .header(HttpHeaders.AUTHORIZATION, "CodeVerification " + Base64.getEncoder().encodeToString((MARIA_SIDOROVA.getPhoneNumber() + ':' + CODE).getBytes()));
        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.accessToken").exists(),
                        jsonPath("$.refreshToken").exists()
                );
    }

    @Test
    void handleAuthenticateByCode_DataIsInvalid_Returns401() throws Exception {
        final var INVALID_CODE = "1112";
        var requestBuilder = post("/api/v1/auth")
                .header(HttpHeaders.AUTHORIZATION,
                        "CodeVerification " + Base64.getEncoder().encodeToString((MARIA_SIDOROVA.getPhoneNumber() + ':' + INVALID_CODE).getBytes()));
        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isUnauthorized()
                );
    }

    @Test
    void handleRefreshToken_DataIsValid_ReturnsNewTokens() throws Exception {
        var requestBuilder = post("/api/v1/auth/refresh")
                .header(HttpHeaders.AUTHORIZATION,
                        "Bearer " + authenticationService.generateRefreshToken(MARIA_SIDOROVA.getPhoneNumber()));
        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.accessToken").exists(),
                        jsonPath("$.refreshToken").exists()
                );
    }

    @Test
    void handleRefreshToken_DataIsValid_ReturnsNewTokensAndOldTokenIsRevoked() throws Exception {
        String oldRefreshToken = authenticationService.generateRefreshToken(MARIA_SIDOROVA.getPhoneNumber());
        var requestBuilder = post("/api/v1/auth/refresh")
                .header(HttpHeaders.AUTHORIZATION,
                        "Bearer " + oldRefreshToken);

        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.accessToken").exists(),
                        jsonPath("$.refreshToken").exists()
                );

        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isUnauthorized()
                );
    }

}