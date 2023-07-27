package ru.qwonix.foxwhiskersapi.entity;

import lombok.Data;

@Data
public class DishType {
    private Long id;
    private String title;
    private Boolean isAvailable = true;
}
