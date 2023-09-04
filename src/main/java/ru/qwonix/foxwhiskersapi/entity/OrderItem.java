package ru.qwonix.foxwhiskersapi.entity;

import lombok.Data;

@Data
public class OrderItem {
    private Order order;
    private Dish dish;
    private Integer count;
}
