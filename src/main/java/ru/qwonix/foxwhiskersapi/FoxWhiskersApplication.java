package ru.qwonix.foxwhiskersapi;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import ru.qwonix.foxwhiskersapi.service.DishService;
import ru.qwonix.foxwhiskersapi.service.impl.ImageDataServiceImpl;

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
