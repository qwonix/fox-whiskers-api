package ru.qwonix.foxwhiskersapi.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.qwonix.foxwhiskersapi.dto.OrderItemDTO;
import ru.qwonix.foxwhiskersapi.entity.*;
import ru.qwonix.foxwhiskersapi.repository.DishRepository;
import ru.qwonix.foxwhiskersapi.repository.OrderItemRepository;
import ru.qwonix.foxwhiskersapi.repository.OrderRepository;
import ru.qwonix.foxwhiskersapi.service.ClientService;
import ru.qwonix.foxwhiskersapi.service.OrderService;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final DishRepository dishRepository;
    private final ClientService clientService;


    public OrderServiceImpl(OrderRepository orderRepository, DishRepository dishRepository, ClientService clientService) {
        this.orderRepository = orderRepository;
        this.dishRepository = dishRepository;
        this.clientService = clientService;
    }

    @Override
    public List<Order> findAllByClientPhoneNumber(String phoneNumber) {
        List<Order> allByClientPhoneNumber = orderRepository.findAllByClientPhoneNumber(phoneNumber);
        log.info("allByClientPhoneNumber - {}", allByClientPhoneNumber);
        return allByClientPhoneNumber;
    }

    @Transactional
    @Override
    public Order create(String phoneNumber, List<OrderItemDTO> items) {
        log.info("items - {}", items);
        Client client = clientService.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new UsernameNotFoundException("Клиент " + phoneNumber + " не зарегистрирован"));

        Order order = Order.builder()
                .client(client)
                .status(OrderStatus.CREATED)
                .orderItems(new ArrayList<>())
                .build();


        for (OrderItemDTO item : items) {
            Dish dish = dishRepository.findById(item.getDishId())
                    .orElseThrow(() -> new IllegalArgumentException("Блюда с id " + item.getDishId() + "не существует"));

            OrderItem e = new OrderItem();
            e.setOrder(order);
            e.setDish(dish);
            e.setCount(item.getCount());
            order.getOrderItems().add(e);
            log.info("e - {}", e);
        }

        Order save = orderRepository.save(order);
        log.info("save - {}", save);
        return save;
    }
}
