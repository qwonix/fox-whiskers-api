package ru.qwonix.foxwhiskersapi.repository;

import ru.qwonix.foxwhiskersapi.entity.PickUpLocation;

import java.util.Optional;

public interface PickUpLocationRepository extends CrudRepository<PickUpLocation, Long> {
    Optional<PickUpLocation> findByPriority(Integer priority);

    Optional<PickUpLocation> findMinPriority();

    Optional<PickUpLocation> findMaxPriority();
}
