package ru.qwonix.foxwhiskersapi.entity;


import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class Dish {
    private Long id;
    private String title;
    private BigDecimal currencyPrice;
    private DishType type;
    private DishDetails dishDetails;
    private Boolean isAvailable;
}

