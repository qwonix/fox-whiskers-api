package ru.qwonix.foxwhiskersapi.rest;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.qwonix.foxwhiskersapi.TestcontainersConfiguration;
import ru.qwonix.foxwhiskersapi.repository.AuthenticationRepository;

import java.time.Duration;
import java.util.Base64;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest(classes = TestcontainersConfiguration.class)
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthenticationRestControllerV1IT {

    public static final String NUMBER = "88005553535";
    public static final String CODE = "1111";

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AuthenticationRepository authenticationRepository;

    @BeforeAll
    public void setUp() {
        authenticationRepository.add(NUMBER, CODE, Duration.ofSeconds(10));
    }


    @Test
    public void handleSendVerificationCode_ReturnsSuccessStatus() throws Exception {
        var requestBuilder = post("/api/v1/auth/code")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "phoneNumber": "+7 999 000 00 00"
                        }
                        """);

        this.mockMvc.perform(requestBuilder)
                .andExpectAll(status().isOk());
    }


    @Test
    public void handleAuthenticateByCode_ValidData_ReturnsTokens() throws Exception {
        var requestBuilder = post("/api/v1/auth")
                .header(HttpHeaders.AUTHORIZATION, "PhoneVerification " + Base64.getEncoder().encodeToString((NUMBER + ':' + CODE).getBytes()));
        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.accessToken").exists(),
                        jsonPath("$.refreshToken").exists()
                );
    }

    @Test
    public void handleAuthenticateByCode_InvalidData_Returns401() throws Exception {
        final var INVALID_CODE = "1112";
        var requestBuilder = post("/api/v1/auth")
                .header(HttpHeaders.AUTHORIZATION,
                        "PhoneVerification " + Base64.getEncoder().encodeToString((NUMBER + ':' + INVALID_CODE).getBytes()));
        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isUnauthorized()
                );
    }

    @Test
    public void handleRefreshToken_ValidData_ReturnsNewTokens() throws Exception {
        final var INVALID_CODE = "1112";
        var requestBuilder = post("/api/v1/auth")
                .header(HttpHeaders.AUTHORIZATION,
                        "PhoneVerification " + Base64.getEncoder().encodeToString((NUMBER + ':' + INVALID_CODE).getBytes()));
        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isUnauthorized()
                );
    }


}