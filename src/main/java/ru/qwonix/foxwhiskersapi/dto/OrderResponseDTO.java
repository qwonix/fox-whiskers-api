package ru.qwonix.foxwhiskersapi.dto;

import lombok.Builder;
import lombok.Data;
import ru.qwonix.foxwhiskersapi.entity.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderResponseDTO {
    private final String id;
    private final String qrCodeData;
    private final Client client;
    private final List<OrderItem> orderItems;
    private final OrderStatus status;
    private final PickUpLocation pickUpLocation;
    private final PaymentMethod paymentMethod;
    private final Double totalPrice;
    private final String expectedReceiptTime;
    private final LocalDateTime created;
}
