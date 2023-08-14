package ru.qwonix.foxwhiskersapi.dto;

import java.util.List;

public record MenuItem(String dishTypeTitle, List<DishDTO> items) {

}
