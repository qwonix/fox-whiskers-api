package ru.qwonix.foxwhiskersapi.service;



import ru.qwonix.foxwhiskersapi.entity.PickUpLocation;

import java.util.List;
import java.util.Optional;

public interface PickUpLocationService {

    List<PickUpLocation> getAll();

    Optional<PickUpLocation> getMaxPriority();

    Optional<PickUpLocation> getMinPriority();

    Optional<PickUpLocation> getByPriority(Integer priority);
}
