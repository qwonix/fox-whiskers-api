package ru.qwonix.foxwhiskersapi.rest;

import org.junit.jupiter.api.Test;
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
import ru.qwonix.foxwhiskersapi.service.AuthenticationService;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@Sql("/sql/user_rest_controller/test_data.sql")
@SpringBootTest(classes = TestcontainersConfiguration.class)
@AutoConfigureMockMvc
class UserRestControllerIT {

    private static final User IVAN_IVANOV = new User(
            UUID.fromString("d28eb809-5f0e-4c94-8c37-45de64cd6d9a"),
            "+7 (999) 123 45-67",
            null,
            null,
            null,
            null,
            UserStatus.ACTIVE,
            Role.INCOMPLETE_REGISTRATION,
            LocalDateTime.of(2023, 5, 25, 15, 32),
            LocalDateTime.of(2023, 5, 25, 15, 32)
    );

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

    private static final User PETR_PETROV = new User(
            UUID.fromString("fa2a9b4c-d285-4c28-9e3d-82c6ed186b45"),
            "+7 (111) 222 33-44",
            "petr_petrov@example.com",
            "Петр",
            "Петров",
            "Петрович",
            UserStatus.ACTIVE,
            Role.CLIENT,
            LocalDateTime.of(2023, 8, 29, 11, 30),
            LocalDateTime.of(2023, 9, 15, 18, 20)


    );

    private static final User VASELISA_SMIRNOVA = new User(
            UUID.fromString("07e26161-d042-44dd-934f-885d1cdd2be7"),
            "+7 (555) 444-33-22",
            "vaselisa_smirnova@example.com",
            "Василиса",
            "Смирнова",
            "Александровна",
            UserStatus.ACTIVE,
            Role.CLIENT,
            LocalDateTime.of(2023, 8, 23, 8, 10),
            LocalDateTime.of(2023, 9, 10, 16, 0)
    );

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AuthenticationService authenticationService;

    void handlePatch_IncompleteRegistration(String requestContent, String expectedResponseContent) throws Exception {
        String accessToken = authenticationService.generateAccessToken(IVAN_IVANOV.getPhoneNumber(), IVAN_IVANOV.getRole().getAuthorities());

        var requestBuilder = patch("/api/v1/user")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestContent);


        this.mockMvc.perform(requestBuilder).andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON),
                content().json(expectedResponseContent)
        );
    }

    @Test
    void handlePatch_IncompleteRegistration_SuccessUpdateAndRoleIsClient() throws Exception {
        handlePatch_IncompleteRegistration("""
                        {
                            "firstName" : "Иван",
                            "lastName" : "Иванов",
                            "middleName" : "Петрович",
                            "email" : "ivan.ivanov@example.com"
                        }
                        """,
                """
                        {
                          "id": "d28eb809-5f0e-4c94-8c37-45de64cd6d9a",
                          "phoneNumber": "+7 (999) 123 45-67",
                          "email": "ivan.ivanov@example.com",
                          "firstName": "Иван",
                          "lastName": "Иванов",
                          "middleName": "Петрович",
                          "status": null,
                          "role": "CLIENT",
                          "created": "2023-05-25T15:32:00",
                          "updated": "2023-05-25T15:32:00"
                        }
                        """
        );
    }

    @Test
    void handlePatch_UpdateLastName_SuccessUpdate() throws Exception {
        handlePatch_IncompleteRegistration("""
                        {
                            "lastName" : "Иванов"
                        }
                        """,
                """
                        {
                          "id": "d28eb809-5f0e-4c94-8c37-45de64cd6d9a",
                          "phoneNumber": "+7 (999) 123 45-67",
                          "email": null,
                          "firstName": null,
                          "lastName": "Иванов",
                          "middleName": null,
                          "status": null,
                          "role": "INCOMPLETE_REGISTRATION",
                          "created": "2023-05-25T15:32:00",
                          "updated": "2023-05-25T15:32:00"
                        }
                        """
        );
    }

    @Test
    void handlePatch_UpdateName_SuccessUpdate() throws Exception {
        handlePatch_IncompleteRegistration("""
                        {
                            "firstName" : "Иван",
                            "lastName" : "Иванов",
                            "middleName" : "Петрович"
                        }
                        """,
                """
                        {
                          "id": "d28eb809-5f0e-4c94-8c37-45de64cd6d9a",
                          "phoneNumber": "+7 (999) 123 45-67",
                          "email": null,
                          "firstName": "Иван",
                          "lastName": "Иванов",
                          "middleName": "Петрович",
                          "status": null,
                          "role": "INCOMPLETE_REGISTRATION",
                          "created": "2023-05-25T15:32:00",
                          "updated": "2023-05-25T15:32:00"
                        }
                        """
        );
    }

//    @Test
//    void handle_UpdateNameInvalidAuthenticatonToken_AccessDenied() {
//        String invalidAccessToken = authenticationService.generateAccessToken(IVAN_IVANOV.getPhoneNumber(), IVAN_IVANOV.getRole().getAuthorities());
//
//        var requestBuilder = patch("/api/v1/user")
//                .header(HttpHeaders.AUTHORIZATION, "Bearer " + invalidAccessToken)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(requestContent);
//
//
//        this.mockMvc.perform(requestBuilder).andExpectAll(
//                status().isOk(),
//                content().contentType(MediaType.APPLICATION_JSON),
//                content().json(expectedResponseContent)
//        );
//    }
}