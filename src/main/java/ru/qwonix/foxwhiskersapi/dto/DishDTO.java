package ru.qwonix.foxwhiskersapi.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class DishDTO {
    private Long id;
    private String title;
    private String shortDescription;
    private Double currencyPrice;
    private String imageUrl;
    private DishTypeDTO type;
}
