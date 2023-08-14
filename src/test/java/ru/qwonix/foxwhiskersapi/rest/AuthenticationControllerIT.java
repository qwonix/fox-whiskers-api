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
import ru.qwonix.foxwhiskersapi.repository.AuthenticationRepository;
import ru.qwonix.foxwhiskersapi.service.AuthenticationService;

import java.time.Duration;
import java.util.Base64;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Sql("/sql/authentication_rest_controller/test_data.sql")
@Transactional
@SpringBootTest(classes = TestcontainersConfiguration.class)
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthenticationControllerIT {

    public static final String CODE = "1111";
    private static final String PHONE_NUMBER = "+7 (999) 123-45-67";
    @Autowired
    MockMvc mockMvc;

    @Autowired
    AuthenticationRepository authenticationRepository;

    @Autowired
    AuthenticationService authenticationService;

    @BeforeAll
    public void setUp() {
        authenticationRepository.add(PHONE_NUMBER, CODE, Duration.ofSeconds(10));
    }


    @Test
    void handleSendVerificationCode_ReturnsSuccessStatus() throws Exception {
        var requestBuilder = post("/api/v1/auth/code")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "phoneNumber": "%s"
                        }
                        """.formatted(PHONE_NUMBER));

        this.mockMvc.perform(requestBuilder)
                .andExpectAll(status().isOk());
    }


    @Test
    void handleAuthenticateByCode_ValidData_ReturnsTokens() throws Exception {
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
    void handleAuthenticateByCode_InvalidData_Returns401() throws Exception {
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
    void handleRefreshToken_ValidData_ReturnsNewTokens() throws Exception {
        var requestBuilder = post("/api/v1/auth/refresh")
                .header(HttpHeaders.AUTHORIZATION,
                        "Bearer " + authenticationService.generateRefreshToken(PHONE_NUMBER));
        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.accessToken").exists(),
                        jsonPath("$.refreshToken").exists()
                );
    }


}