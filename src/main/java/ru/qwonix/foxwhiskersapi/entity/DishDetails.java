package ru.qwonix.foxwhiskersapi.entity;

import lombok.Data;

@Data
public class DishDetails {
    private Long id;
    private String compositionText;
    private String measureText;
    private ImageData imageData;
}
