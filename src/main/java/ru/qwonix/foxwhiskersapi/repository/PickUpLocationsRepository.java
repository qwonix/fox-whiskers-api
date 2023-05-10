package ru.qwonix.foxwhiskersapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.qwonix.foxwhiskersapi.entity.DishDetails;
import ru.qwonix.foxwhiskersapi.entity.PickUpLocation;

public interface PickUpLocationsRepository extends JpaRepository<PickUpLocation, Long> {
}
