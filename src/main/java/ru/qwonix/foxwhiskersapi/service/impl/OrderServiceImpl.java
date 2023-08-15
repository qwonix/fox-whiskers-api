package ru.qwonix.foxwhiskersapi.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.qwonix.foxwhiskersapi.dto.OrderItemDTO;
import ru.qwonix.foxwhiskersapi.entity.*;
import ru.qwonix.foxwhiskersapi.repository.DishRepository;
import ru.qwonix.foxwhiskersapi.repository.OrderRepository;
import ru.qwonix.foxwhiskersapi.repository.PickUpLocationRepository;
import ru.qwonix.foxwhiskersapi.service.UserService;
import ru.qwonix.foxwhiskersapi.service.OrderService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final DishRepository dishRepository;
    private final PickUpLocationRepository pickUpLocationRepository;
    private final UserService userService;


    public OrderServiceImpl(OrderRepository orderRepository, DishRepository dishRepository, PickUpLocationRepository pickUpLocationRepository, UserService userService) {
        this.orderRepository = orderRepository;
        this.dishRepository = dishRepository;
        this.pickUpLocationRepository = pickUpLocationRepository;
        this.userService = userService;
    }

    @Override
    public List<Order> findAllByPhoneNumber(String phoneNumber) {
        return orderRepository.findAllByPhoneNumber(phoneNumber);
    }

    @Override
    public Order create(String phoneNumber, List<OrderItemDTO> items, Long pickUpLocationId, PaymentMethod paymentMethod) {
        User user = userService.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new UsernameNotFoundException("Клиент " + phoneNumber + " не зарегистрирован"));

        PickUpLocation pickUpLocation = pickUpLocationRepository.find(pickUpLocationId)
                .orElseThrow(() -> new IllegalArgumentException("Пункта выдачи с id " + pickUpLocationId + " не существует"));

        Order order = Order.builder()
                .user(user)
                .status(OrderStatus.CREATED)
                .created(LocalDateTime.now())
                .paymentMethod(paymentMethod)
                .orderItems(new ArrayList<>())
                .receivingCode(String.valueOf(new Random().nextInt(8999) + 1000))
                .pickUpLocation(pickUpLocation)
                .build();


        for (OrderItemDTO item : items) {
            Dish dish = dishRepository.find(item.dishId())
                    .orElseThrow(() -> new IllegalArgumentException("Блюда с id " + item.dishId() + "не существует"));

            OrderItem e = new OrderItem();
            e.setOrder(order);
            e.setDish(dish);
            e.setCount(item.count());
            order.getOrderItems().add(e);
        }

        return orderRepository.insert(order);
    }
}
