package ru.qwonix.foxwhiskersapi.service;


import ru.qwonix.foxwhiskersapi.entity.PickUpLocation;
import ru.qwonix.foxwhiskersapi.operation.FindPickUpLocation;

import java.util.List;

public interface PickUpLocationService {
    List<PickUpLocation> getAll();

    FindPickUpLocation.Result getMaxPriority();

    FindPickUpLocation.Result getMinPriority();

    FindPickUpLocation.Result getByPriority(Integer priority);
}
