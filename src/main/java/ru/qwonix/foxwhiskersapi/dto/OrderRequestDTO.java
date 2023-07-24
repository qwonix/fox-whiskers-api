package ru.qwonix.foxwhiskersapi.dto;

import ru.qwonix.foxwhiskersapi.entity.PaymentMethod;

import java.util.List;

public record OrderRequestDTO(String phoneNumber, List<OrderItemDTO> orderItems, Long pickUpLocationId,
                              PaymentMethod paymentMethod) {
}
