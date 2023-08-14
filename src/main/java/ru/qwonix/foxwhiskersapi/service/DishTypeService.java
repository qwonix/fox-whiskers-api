package ru.qwonix.foxwhiskersapi.service;

import ru.qwonix.foxwhiskersapi.entity.DishType;

import java.util.List;

public interface DishTypeService {

    List<DishType> findAll();

}
