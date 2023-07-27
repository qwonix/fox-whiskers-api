package ru.qwonix.foxwhiskersapi.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class OrderItem {
    private Long id;
    @JsonIgnore
    private Order order;
    private Dish dish;
    private Integer count;

}
