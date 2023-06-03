package ru.qwonix.foxwhiskersapi.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.qwonix.foxwhiskersapi.dto.OrderRequestDTO;
import ru.qwonix.foxwhiskersapi.dto.OrderResponseDTO;
import ru.qwonix.foxwhiskersapi.dto.OrdersDTO;
import ru.qwonix.foxwhiskersapi.entity.Order;
import ru.qwonix.foxwhiskersapi.entity.OrderItem;
import ru.qwonix.foxwhiskersapi.service.OrderService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/v1/order")
public class OrderRestController {

    private final OrderService orderService;

    public OrderRestController(OrderService orderService) {
        this.orderService = orderService;
    }


    @PostMapping
    public ResponseEntity<List<OrderResponseDTO>> byPhoneNumber(@RequestBody OrdersDTO ordersDTO) {
        log.info("GET all orders by client phone number");
        List<Order> orders = orderService.findAllByClientPhoneNumber(ordersDTO.getPhoneNumber());
        List<OrderResponseDTO> orderResponse = orders.stream().map(order -> {
            BigDecimal totalPrice = BigDecimal.ZERO;
            for (OrderItem orderItem : order.getOrderItems()) {
                BigDecimal itemPrice = orderItem.getDish().getCurrencyPrice();
                int itemCount = orderItem.getCount();
                BigDecimal subtotal = itemPrice.multiply(new BigDecimal(itemCount));
                totalPrice = totalPrice.add(subtotal);
            }

            Long id = order.getId();
            String formattedId = String.format("%03d-%04d", id / 10000, id % 10000);
            LocalDateTime created = order.getCreated() == null ? LocalDateTime.now() : order.getCreated();
            return OrderResponseDTO.builder()
                    .id(formattedId)
                    .client(order.getClient())
                    .orderItems(order.getOrderItems())
                    .status(order.getStatus())
                    .pickUpLocation(order.getPickUpLocation())
                    .paymentMethod(order.getPaymentMethod())
                    .totalPrice(totalPrice.doubleValue())
                    .expectedReceiptTime(created.plusMinutes(20).format(DateTimeFormatter.ofPattern("hh:mm")))
                    .build();
        }).collect(Collectors.toList());
        return ResponseEntity.ok(orderResponse);
    }


    @PutMapping
    public ResponseEntity<Order> create(@RequestBody OrderRequestDTO request) {
        Order body = orderService.create(
                request.getPhoneNumber(),
                request.getOrderItems(),
                request.getPickUpLocationId(),
                request.getPaymentMethod());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(body);
    }
}
