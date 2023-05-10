package ru.qwonix.foxwhiskersapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.qwonix.foxwhiskersapi.entity.Dish;

import java.util.List;

public interface DishRepository extends JpaRepository<Dish, Long> {

    List<Dish> findByType_Id(Long id);
}
