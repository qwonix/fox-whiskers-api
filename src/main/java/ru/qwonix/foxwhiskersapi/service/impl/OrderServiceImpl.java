package ru.qwonix.foxwhiskersapi.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.qwonix.foxwhiskersapi.dto.OrderItemDTO;
import ru.qwonix.foxwhiskersapi.entity.*;
import ru.qwonix.foxwhiskersapi.operation.CreateOrder;
import ru.qwonix.foxwhiskersapi.repository.DishRepository;
import ru.qwonix.foxwhiskersapi.repository.OrderRepository;
import ru.qwonix.foxwhiskersapi.repository.PickUpLocationRepository;
import ru.qwonix.foxwhiskersapi.service.UserService;
import ru.qwonix.foxwhiskersapi.service.OrderService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class OrderServiceImpl implements OrderService {

    OrderRepository orderRepository;
    DishRepository dishRepository;
    PickUpLocationRepository pickUpLocationRepository;
    UserService userService;

    @Override
    public List<Order> findAllByUsername(String phoneNumber) {
        return orderRepository.findAllByPhoneNumber(phoneNumber);
    }

    @Override
    public CreateOrder.Result create(String phoneNumber, List<OrderItemDTO> items, Long pickUpLocationId, PaymentMethod paymentMethod) {
        var optionalUser = userService.findByPhoneNumber(phoneNumber);
        if (optionalUser.isEmpty()) {
            return CreateOrder.Result.userNotFound();
        }
        var user = optionalUser.get();

        var optionalPickUpLocation = pickUpLocationRepository.find(pickUpLocationId);
        if (optionalPickUpLocation.isEmpty()) {
            return CreateOrder.Result.pickUpLocationNotFound();
        }
        var pickUpLocation = optionalPickUpLocation.get();

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

        var insert = orderRepository.insert(order);
        return CreateOrder.Result.success(insert);
    }
}
