package ru.qwonix.foxwhiskersapi.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.qwonix.foxwhiskersapi.TestcontainersConfiguration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@Sql("/sql/image_rest_controller/test_data.sql")
@SpringBootTest(classes = TestcontainersConfiguration.class)
@Transactional
@AutoConfigureMockMvc
class ImageRestControllerIT {
    public static final String IMAGE_2 = "img/image_rest_controller/image_2.png";
    public static final String IMAGE_3 = "img/image_rest_controller/image_3.png";

    @Autowired
    MockMvc mockMvc;

    @Test
    void handleGet_ValidImageName_ReturnValidResponse() throws Exception {
        var requestBuilder = get("/api/v1/image/image_1");

        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.IMAGE_PNG)
                );
    }

    @Test
    void handleUpload_ValidImage_ReturnValidResponse() throws Exception {
        var image3 = new MockMultipartFile(
                "image",
                "image.png",
                MediaType.IMAGE_PNG_VALUE,
                new ClassPathResource(IMAGE_3).getInputStream()
        );

        var requestBuilder = multipart("/api/v1/image/upload")
                .file(image3)
                .contentType(MediaType.IMAGE_PNG);

        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isCreated(),
                        header().exists(HttpHeaders.LOCATION)
                );
    }


    @Test
    void handleUpload_DuplicateImageName_ReturnValidResponse() throws Exception {
        var image2 = new MockMultipartFile(
                "image",
                "image_2.png",
                MediaType.IMAGE_PNG_VALUE,
                new ClassPathResource(IMAGE_2).getInputStream()
        );

        var requestBuilder = multipart("/api/v1/image/upload")
                .file(image2)
                .contentType(MediaType.IMAGE_PNG);

        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isCreated(),
                        header().exists(HttpHeaders.LOCATION)
                );
    }


}