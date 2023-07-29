package ru.qwonix.foxwhiskersapi.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.qwonix.foxwhiskersapi.TestcontainersConfiguration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql("/sql/pick_up_location_rest_controller/test_data.sql")
@Transactional
@SpringBootTest(classes = TestcontainersConfiguration.class)
@AutoConfigureMockMvc(printOnlyOnFailure = false)
class PickUpLocationRestControllerIT {
    @Autowired
    MockMvc mockMvc;

    @Test
    public void handleAll_ReturnsValidResponseEntity() throws Exception {
        var requestBuilder = get("/api/v1/location");

        this.mockMvc.perform(requestBuilder)
                .andExpectAll(status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                                [
                                     {
                                       "id": 1,
                                       "title": "ТРК «Лиговъ»",
                                       "priority": 1,
                                       "cityName": "Санкт-Петербург",
                                       "streetName": "Лиговский проспект",
                                       "houseData": "153",
                                       "additionalInformation": "Ежедневно: 10:30 – 21:30\\nТелефон: +7 (999) 203–88–30",
                                       "latitude": 59.914899,
                                       "longitude": 30.349349
                                     },
                                     {
                                       "id": 2,
                                       "title": "ТРК «Сенной»",
                                       "priority": 5,
                                       "cityName": "Санкт-Петербург",
                                       "streetName": "Ефимова",
                                       "houseData": "3С",
                                       "additionalInformation": "Ежедневно: 10:30 – 21:30\\nТелефон: +7 (999) 203–88–30",
                                       "latitude": 59.924511,
                                       "longitude": 30.320845
                                     }
                                ]
                                """)
                );
    }

    @Test
    public void handlePriority_ParamIsMax_ReturnsValidResponseEntity() throws Exception {
        var requestBuilder = get("/api/v1/location?priority=max");

        this.mockMvc.perform(requestBuilder)
                .andExpectAll(status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                  "id": 1,
                                  "title": "ТРК «Лиговъ»",
                                  "priority": 1,
                                  "cityName": "Санкт-Петербург",
                                  "streetName": "Лиговский проспект",
                                  "houseData": "153",
                                  "additionalInformation": "Ежедневно: 10:30 – 21:30\\nТелефон: +7 (999) 203–88–30",
                                  "latitude": 59.914899,
                                  "longitude": 30.349349
                                }                                
                                """)
                );
    }


    @Test
    public void handlePriority_ParamIsPriorityValue_ReturnsValidResponseEntity() throws Exception {
        var requestBuilder = get("/api/v1/location?priority=5");

        this.mockMvc.perform(requestBuilder)
                .andExpectAll(status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                       "id": 2,
                                       "title": "ТРК «Сенной»",
                                       "priority": 5,
                                       "cityName": "Санкт-Петербург",
                                       "streetName": "Ефимова",
                                       "houseData": "3С",
                                       "additionalInformation": "Ежедневно: 10:30 – 21:30\\nТелефон: +7 (999) 203–88–30",
                                       "latitude": 59.924511,
                                       "longitude": 30.320845
                                }
                                """)
                );
    }

    @Test
    public void handlePriority_ParamIsPriorityValueButNotExists_ReturnsValidResponseCode() throws Exception {
        var requestBuilder = get("/api/v1/location?priority=3");

        this.mockMvc.perform(requestBuilder)
                .andExpectAll(status().isNotFound());
    }

    @Test
    public void handlePriority_ParamIsInvalid_ReturnsValidResponseCode() throws Exception {
        var requestBuilder = get("/api/v1/location?priority=hello");

        this.mockMvc.perform(requestBuilder)
                .andExpectAll(status().isBadRequest());
    }

}