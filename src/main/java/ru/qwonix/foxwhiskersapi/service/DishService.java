package ru.qwonix.foxwhiskersapi.service;

import ru.qwonix.foxwhiskersapi.entity.Dish;

import java.util.List;
import java.util.Optional;

public interface DishService {

    void save(Dish dish);

    List<Dish> findAll();

    Optional<Dish> findById(Long id);
}
