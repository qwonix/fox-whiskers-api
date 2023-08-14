package ru.qwonix.foxwhiskersapi.dto;


public record DishDTO(Long id, String title, String shortDescription, Double currencyPrice, String imageUrl) {
}
