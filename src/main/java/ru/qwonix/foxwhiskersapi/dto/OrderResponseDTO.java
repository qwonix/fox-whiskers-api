package ru.qwonix.foxwhiskersapi.dto;

import ru.qwonix.foxwhiskersapi.entity.*;

import java.time.LocalDateTime;
import java.util.List;

public record OrderResponseDTO(String id, String qrCodeData, List<OrderItem> orderItems,
                               OrderStatus status, PickUpLocation pickUpLocation, PaymentMethod paymentMethod,
                               Double totalPrice, String expectedReceiptTime, LocalDateTime created) {
}
