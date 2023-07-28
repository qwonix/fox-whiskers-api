package ru.qwonix.foxwhiskersapi.repository;


import ru.qwonix.foxwhiskersapi.entity.Dish;

import java.util.List;

public interface DishRepository extends CrudRepository<Dish, Long> {

    List<Dish> findByType_Id(Long id);
}
