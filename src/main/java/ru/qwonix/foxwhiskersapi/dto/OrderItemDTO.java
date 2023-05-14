package ru.qwonix.foxwhiskersapi.dto;

import lombok.Data;


@Data
public class OrderItemDTO {
    private final Long dishId;
    private final Integer count;
}
