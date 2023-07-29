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
        return pickUpLocationRepository.findMinPriority();
    }

    @Override
    public Optional<PickUpLocation> getMinPriority() {
        return pickUpLocationRepository.findMaxPriority();

    }

    @Override
    public Optional<PickUpLocation> getByPriority(Integer priority) {
        return pickUpLocationRepository.findByPriority(priority);
    }
}
