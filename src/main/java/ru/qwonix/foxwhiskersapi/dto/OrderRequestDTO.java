package ru.qwonix.foxwhiskersapi.dto;

import lombok.Data;
import ru.qwonix.foxwhiskersapi.entity.PaymentMethod;

import java.util.List;

@Data
public class OrderRequestDTO {
    private final String phoneNumber;
    private final List<OrderItemDTO> orderItems;
    private final Long pickUpLocationId;
    private final PaymentMethod paymentMethod;
}
