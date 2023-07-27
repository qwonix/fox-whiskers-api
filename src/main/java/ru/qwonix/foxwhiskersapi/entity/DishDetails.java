package ru.qwonix.foxwhiskersapi.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class DishDetails {
    private Long id;
    private String compositionText;
    private String measureText;
    @JsonIgnore
    private Dish dish;
    private ImageData imageData;
}
