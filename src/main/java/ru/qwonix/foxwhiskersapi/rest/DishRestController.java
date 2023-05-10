package ru.qwonix.foxwhiskersapi.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.qwonix.foxwhiskersapi.dto.DishDTO;
import ru.qwonix.foxwhiskersapi.dto.DishTypeDTO;
import ru.qwonix.foxwhiskersapi.entity.Dish;
import ru.qwonix.foxwhiskersapi.service.DishService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/v1/dish")
public class DishRestController {

    private final DishService dishService;

    public DishRestController(DishService dishService) {
        this.dishService = dishService;
    }

    @GetMapping()
    public ResponseEntity<List<DishDTO>> all() {
        List<Dish> all = dishService.findAll();

        return ResponseEntity.ok(all.stream().map(dish -> new DishDTO(
                dish.getId(),
                dish.getTitle(),
                dish.getDishDetails().getMeasureText(),
                dish.getCurrencyPrice().doubleValue(),
                "https://sun9-67.userapi.com/vpAGHIpMA6UjSml6ahcQn0je_DiLa3GRcNT0TQ/K_w6dLtJ7CM.jpg",
                new DishTypeDTO(
                        dish.getType().getId(),
                        dish.getType().getTitle()
                )
        )).collect(Collectors.toList()));
    }


    @GetMapping("/{id}")
    public ResponseEntity<Dish> one(@PathVariable("id") Long id) {
        log.info("one dish");
        return ResponseEntity.of(dishService.findById(id));
    }
}
