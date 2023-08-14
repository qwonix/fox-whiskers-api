package ru.qwonix.foxwhiskersapi.entity;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class DishDetails {
    private String compositionText;
    private String measureText;
    private String imageName;
}
