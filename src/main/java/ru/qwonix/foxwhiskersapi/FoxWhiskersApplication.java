package ru.qwonix.foxwhiskersapi;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import ru.qwonix.foxwhiskersapi.entity.Dish;
import ru.qwonix.foxwhiskersapi.entity.DishDetails;
import ru.qwonix.foxwhiskersapi.entity.DishType;
import ru.qwonix.foxwhiskersapi.entity.ImageData;
import ru.qwonix.foxwhiskersapi.service.DishService;
import ru.qwonix.foxwhiskersapi.service.impl.ImageDataServiceImpl;

import java.math.BigDecimal;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class FoxWhiskersApplication {

    public static void main(String[] args) {
        SpringApplication.run(FoxWhiskersApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ImageDataServiceImpl imageDataServiceImpl, DishService dishService) {
        return args -> {
//            ImageData imageData = imageDataServiceImpl.uploadImage(Paths.get("C:\\Users\\admin\\Desktop\\pizza\\small.png"));
        };
    }
}
