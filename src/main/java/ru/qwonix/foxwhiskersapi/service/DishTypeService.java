package ru.qwonix.foxwhiskersapi.service;

import ru.qwonix.foxwhiskersapi.entity.DishType;

import java.util.List;
import java.util.Optional;

public interface DishTypeService {

    List<DishType> findAll();

}
