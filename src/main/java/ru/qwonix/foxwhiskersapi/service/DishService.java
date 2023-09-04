package ru.qwonix.foxwhiskersapi.service;

import org.springframework.web.util.UriComponentsBuilder;
import ru.qwonix.foxwhiskersapi.operation.FindDish;
import ru.qwonix.foxwhiskersapi.operation.FindMenu;

public interface DishService {

    FindMenu.Result findMenu(UriComponentsBuilder builder);

    FindDish.Result findById(Long id);
}
