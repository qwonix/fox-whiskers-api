package ru.qwonix.foxwhiskersapi.service;

import ru.qwonix.foxwhiskersapi.entity.Dish;
import ru.qwonix.foxwhiskersapi.entity.DishDetails;
import ru.qwonix.foxwhiskersapi.entity.DishType;

import java.util.List;
import java.util.Optional;

public interface DishTypeService {

    void save(List<DishType> dishType);

    List<DishType> findAll();

    Optional<DishType> findById(Long id);
}
