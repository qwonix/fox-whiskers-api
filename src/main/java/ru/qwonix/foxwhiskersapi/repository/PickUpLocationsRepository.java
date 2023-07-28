package ru.qwonix.foxwhiskersapi.repository;

import ru.qwonix.foxwhiskersapi.entity.PickUpLocation;

import java.util.Optional;

public interface PickUpLocationsRepository extends CrudRepository<PickUpLocation, Long> {
    Optional<PickUpLocation> findFirstByOrderByPriorityDesc();
}
