package ru.qwonix.foxwhiskersapi.service;

import ru.qwonix.foxwhiskersapi.dto.OrderItemDTO;
import ru.qwonix.foxwhiskersapi.entity.Order;
import ru.qwonix.foxwhiskersapi.entity.PaymentMethod;

import java.util.List;

public interface OrderService {
    List<Order> findAllByPhoneNumber(String phoneNumber);

    Order create(String phoneNumber, List<OrderItemDTO> items, Long pickUpLocationId, PaymentMethod paymentMethod);
}
