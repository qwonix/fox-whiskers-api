package ru.qwonix.foxwhiskersapi.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import ru.qwonix.foxwhiskersapi.dto.MenuItemDTO;
import ru.qwonix.foxwhiskersapi.entity.Dish;
import ru.qwonix.foxwhiskersapi.operation.FindDish;
import ru.qwonix.foxwhiskersapi.service.DishService;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/api/v1/dish")
public class DishRestController {

    private final DishService dishService;

    @GetMapping
    public ResponseEntity<List<MenuItemDTO>> getMenu(UriComponentsBuilder builder) {
        return dishService.findMenu(builder).process(result -> ResponseEntity.ok(result.menuItems()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDishById(@PathVariable("id") Long id) {
        return dishService.findById(id).process(new FindDish.Result.Processor<>() {
            @Override
            public ResponseEntity<Dish> processSuccess(FindDish.Result.Success result) {
                return ResponseEntity.ok(result.dish());
            }

            @Override
            public ResponseEntity<?> processNotFound(FindDish.Result.NotFound result) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("no dish with this id");
            }
        });
    }
}
