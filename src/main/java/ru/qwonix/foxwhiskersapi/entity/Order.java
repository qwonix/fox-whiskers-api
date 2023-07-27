package ru.qwonix.foxwhiskersapi.entity;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class Order {

    private Long id;
    private Client client;
    private OrderStatus status;
    private String receivingCode;
    private List<OrderItem> orderItems;
    private PickUpLocation pickUpLocation;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private LocalDateTime created;
    private LocalDateTime updated;
}
