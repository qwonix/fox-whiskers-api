package ru.qwonix.foxwhiskersapi.rest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
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

@Transactional
@Sql("/sql/dish_rest_controller/test_data.sql")
@SpringBootTest(classes = TestcontainersConfiguration.class)
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DishRestControllerIT {


    @Autowired
    MockMvc mockMvc;

    @Test
    void handleAll_ReturnsValidEntities() throws Exception {
        var requestBuilder = get("/api/v1/dish");

        this.mockMvc.perform(requestBuilder).andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON),
                content().json("""
                        [
                          {
                            "dishTypeTitle": "Пицца 42см",
                            "items": [
                              {
                                "id": 3,
                                "title": "Кур Маслино",
                                "shortDescription": "целая, 42 см, 1350 грамм",
                                "currencyPrice": 552.0,
                                "imageUrl": "http://localhost/api/v1/image/image_1"
                              },
                              {
                                "id": 2,
                                "title": "Маргарита",
                                "shortDescription": "целая, 42 см, 1470 грамм",
                                "currencyPrice": 520.0,
                                "imageUrl": "http://localhost/api/v1/image/image_1"
                              },
                              {
                                "id": 1,
                                "title": "Ранч",
                                "shortDescription": "целая, 42 см",
                                "currencyPrice": 599.0,
                                "imageUrl": "http://localhost/api/v1/image/image_1"
                              }
                            ]
                          },
                          {
                            "dishTypeTitle": "Супы",
                            "items": [
                              {
                                "id": 13,
                                "title": "Куриный суп",
                                "shortDescription": "330 мл",
                                "currencyPrice": 69.0,
                                "imageUrl": "http://localhost/api/v1/image/image_2"
                              },
                              {
                                "id": 12,
                                "title": "Сырный суп",
                                "shortDescription": "330 мл",
                                "currencyPrice": 69.0,
                                "imageUrl": "http://localhost/api/v1/image/image_2"
                              },
                              {
                                "id": 11,
                                "title": "Борщ",
                                "shortDescription": "330 мл",
                                "currencyPrice": 69.0,
                                "imageUrl": "http://localhost/api/v1/image/image_2"
                              }
                            ]
                          }
                        ]
                        """)
        );
    }

    @Test
    void handleOne_ReturnsValidEntity() throws Exception {
        var requestBuilder = get("/api/v1/dish/1");

        this.mockMvc.perform(requestBuilder).andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON),
                content().json("""
                        {
                           "id": 1,
                           "title": "Ранч",
                           "currencyPrice": 599.00,
                           "type": {
                             "id": 1,
                             "title": "Пицца 42см",
                             "isAvailable": true
                           },
                           "dishDetails": {
                             "compositionText": "Фирменный томатный соус, моцарелла, томатные слайсы, цыпленок ранч",
                             "measureText": "целая, 42 см",
                             "imageName": "image_1"
                           },
                           "isAvailable": true
                        }
                        """)
        );
    }
}