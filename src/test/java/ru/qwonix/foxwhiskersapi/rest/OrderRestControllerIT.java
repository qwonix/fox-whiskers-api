package ru.qwonix.foxwhiskersapi.rest;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.qwonix.foxwhiskersapi.TestcontainersConfiguration;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@Sql("/sql/order_rest_controller/test_data.sql")
@SpringBootTest(classes = TestcontainersConfiguration.class)
@AutoConfigureMockMvc
class OrderRestControllerIT {

    @Test
    void handleGetByUsername_DataIsValid_ReturnsValid() {

    }
}