package ru.qwonix.foxwhiskersapi.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import ru.qwonix.foxwhiskersapi.dto.DishDTO;
import ru.qwonix.foxwhiskersapi.dto.MenuItemDTO;
import ru.qwonix.foxwhiskersapi.entity.Dish;
import ru.qwonix.foxwhiskersapi.entity.DishType;
import ru.qwonix.foxwhiskersapi.operation.FindDish;
import ru.qwonix.foxwhiskersapi.operation.FindMenu;
import ru.qwonix.foxwhiskersapi.repository.DishRepository;
import ru.qwonix.foxwhiskersapi.rest.ImageRestController;
import ru.qwonix.foxwhiskersapi.service.DishService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class DishServiceImpl implements DishService {

    DishRepository dishRepository;

    @Override
    public FindMenu.Result findMenu(UriComponentsBuilder builder) {
        Map<DishType, List<Dish>> dishTypeToDishesMap = dishRepository.findAll().stream().
                collect(Collectors.groupingBy(Dish::getType));

        var imageUriPath = builder.path(ImageRestController.IMAGE_CONTROLLER_PATH + "/{imageName}");
        // TODO: 04-Sep-23 to mapper
        var menuItems = new ArrayList<MenuItemDTO>();
        dishTypeToDishesMap.forEach((dishType, dishes) -> {
            var dishDTOs = dishes.stream().map(dish -> new DishDTO(
                    dish.getId(),
                    dish.getTitle(),
                    dish.getDishDetails().getMeasureText(),
                    dish.getCurrencyPrice().doubleValue(),
                    imageUriPath.build(Map.of("imageName", dish.getDishDetails().getImageName())).toString()
            )).toList();

            menuItems.add(new MenuItemDTO(dishType.getTitle(), dishDTOs));
        });
        return FindMenu.Result.success(menuItems);
    }

    @Override
    public FindDish.Result findById(Long id) {
        var optionalDish = dishRepository.find(id);
        // TODO: 04-Sep-23 to dto and mapper
        if (optionalDish.isPresent()) {
            return FindDish.Result.success(optionalDish.get());
        } else {
            return FindDish.Result.notFound();
        }
    }
}
