package ru.qwonix.foxwhiskersapi.service;

import ru.qwonix.foxwhiskersapi.dto.OrderItemDTO;
import ru.qwonix.foxwhiskersapi.entity.Order;

import java.util.List;

public interface OrderService {
    List<Order> findAllByClientPhoneNumber(String phoneNumber);

    Order create(String phoneNumber, List<OrderItemDTO> items);
}
