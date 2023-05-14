package ru.qwonix.foxwhiskersapi.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderRequestDTO {
    private final String phoneNumber;
    private final List<OrderItemDTO> items;
}
