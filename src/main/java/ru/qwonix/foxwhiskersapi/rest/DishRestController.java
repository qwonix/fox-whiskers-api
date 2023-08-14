package ru.qwonix.foxwhiskersapi.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import ru.qwonix.foxwhiskersapi.dto.DishDTO;
import ru.qwonix.foxwhiskersapi.dto.MenuItem;
import ru.qwonix.foxwhiskersapi.entity.Dish;
import ru.qwonix.foxwhiskersapi.entity.DishType;
import ru.qwonix.foxwhiskersapi.service.DishService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/v1/dish")
public class DishRestController {

    private final DishService dishService;

    public DishRestController(DishService dishService) {
        this.dishService = dishService;
    }

    @GetMapping
    public ResponseEntity<List<MenuItem>> all(UriComponentsBuilder builder) {
        Map<DishType, List<Dish>> dishTypeToDishesMap = dishService.findAll().stream().
                collect(Collectors.groupingBy(Dish::getType));

        var imageUriPath = builder.path("/api/v1/image/{imageName}");
        var menuItems = new ArrayList<MenuItem>();
        dishTypeToDishesMap.forEach((dishType, dishes) -> {
            var dishDTOs = dishes.stream().map(dish -> new DishDTO(
                    dish.getId(),
                    dish.getTitle(),
                    dish.getDishDetails().getMeasureText(),
                    dish.getCurrencyPrice().doubleValue(),
                    imageUriPath.build(Map.of("imageName", dish.getDishDetails().getImageName())).toString()
            )).toList();

            menuItems.add(new MenuItem(dishType.getTitle(), dishDTOs));
        });

        return ResponseEntity.ok(menuItems);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Dish> one(@PathVariable("id") Long id) {
        return ResponseEntity.of(dishService.findById(id));
    }
}
