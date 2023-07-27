package ru.qwonix.foxwhiskersapi.entity;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class Dish {
    private Long id;
    private String title;
    private BigDecimal currencyPrice;
    private DishType type;
    private DishDetails dishDetails;
    private Boolean isAvailable;
}

