package ru.qwonix.foxwhiskersapi.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.qwonix.foxwhiskersapi.dto.OrderRequestDTO;
import ru.qwonix.foxwhiskersapi.entity.Order;
import ru.qwonix.foxwhiskersapi.service.OrderService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/order")
public class OrderRestController {

    private final OrderService orderService;

    public OrderRestController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PreAuthorize("")
    @PostMapping
    public ResponseEntity<List<Order>> byPhoneNumber(@RequestBody String phoneNumber) {
        return ResponseEntity.ok(orderService.findAllByClientPhoneNumber(phoneNumber));
    }

    @PreAuthorize("")
    @PutMapping
    public ResponseEntity<Order> create(@RequestBody OrderRequestDTO request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(orderService.create(request.getPhoneNumber(), request.getItems()));
    }

}
