package ru.qwonix.foxwhiskersapi.dto;

import java.util.List;

public record MenuItem(String title, List<DishDTO> items) {

}
