package ru.qwonix.foxwhiskersapi.service.impl;

import org.springframework.stereotype.Service;
import ru.qwonix.foxwhiskersapi.entity.Dish;
import ru.qwonix.foxwhiskersapi.repository.DishRepository;
import ru.qwonix.foxwhiskersapi.service.DishService;

import java.util.List;
import java.util.Optional;

@Service
public class DishServiceImpl implements DishService {

    private final DishRepository dishRepository;

    public DishServiceImpl(DishRepository dishRepository) {
        this.dishRepository = dishRepository;
    }

    @Override
    public void save(Dish dish) {
        dishRepository.save(dish);
    }

    @Override
    public List<Dish> findAll() {
        return dishRepository.findAll();
    }

    @Override
    public Optional<Dish> findById(Long id) {
        return dishRepository.findById(id);
    }
}
