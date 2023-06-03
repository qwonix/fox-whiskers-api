package ru.qwonix.foxwhiskersapi.dto;

import lombok.Data;

import java.util.List;

@Data
public class MenuItem {
    private final String title;
    private final List<DishDTO> items;
}
