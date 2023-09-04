package ru.qwonix.foxwhiskersapi.service.impl;

import org.springframework.stereotype.Service;
import ru.qwonix.foxwhiskersapi.entity.PickUpLocation;
import ru.qwonix.foxwhiskersapi.repository.PickUpLocationRepository;
import ru.qwonix.foxwhiskersapi.service.PickUpLocationService;

import java.util.List;
import java.util.Optional;

@Service
public class PickUpLocationServiceImpl implements PickUpLocationService {

    private final PickUpLocationRepository pickUpLocationRepository;

    public PickUpLocationServiceImpl(PickUpLocationRepository pickUpLocationRepository) {
        this.pickUpLocationRepository = pickUpLocationRepository;
    }

    @Override
    public List<PickUpLocation> getAll() {
        return pickUpLocationRepository.findAll();
    }

    @Override
    public Optional<PickUpLocation> getMaxPriority() {
        // descending order: 1 is higher priority than 2
        return pickUpLocationRepository.findMinPriority();
    }

    @Override
    public Optional<PickUpLocation> getMinPriority() {
        // descending order: 2 is lower priority than 1
        return pickUpLocationRepository.findMaxPriority();
    }

    @Override
    public Optional<PickUpLocation> getByPriority(Integer priority) {
        return pickUpLocationRepository.findByPriority(priority);
    }
}
