package ru.qwonix.foxwhiskersapi.entity;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class DishType {
    private Long id;
    private String title;
    private Boolean isAvailable = true;
}
