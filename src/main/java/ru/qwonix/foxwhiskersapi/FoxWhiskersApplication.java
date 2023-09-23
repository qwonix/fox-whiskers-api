package ru.qwonix.foxwhiskersapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import ru.qwonix.foxwhiskersapi.config.JwtTokenProperties;

@SpringBootApplication
@ConfigurationPropertiesScan(basePackages = {"ru.qwonix.foxwhiskersapi.config"})
@EnableConfigurationProperties(value = {JwtTokenProperties.class})
public class FoxWhiskersApplication {

    public static void main(String[] args) {
        SpringApplication.run(FoxWhiskersApplication.class, args);
    }

}
