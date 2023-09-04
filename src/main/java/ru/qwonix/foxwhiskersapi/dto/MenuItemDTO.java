package ru.qwonix.foxwhiskersapi.dto;

import java.util.List;

public record MenuItemDTO(String dishTypeTitle, List<DishDTO> items) {

}
