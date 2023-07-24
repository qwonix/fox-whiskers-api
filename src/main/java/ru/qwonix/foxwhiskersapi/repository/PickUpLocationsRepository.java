package ru.qwonix.foxwhiskersapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.qwonix.foxwhiskersapi.entity.PickUpLocation;

import java.util.Optional;

public interface PickUpLocationsRepository extends JpaRepository<PickUpLocation, Long> {
    Optional<PickUpLocation> findFirstByOrderByPriorityDesc();
}
