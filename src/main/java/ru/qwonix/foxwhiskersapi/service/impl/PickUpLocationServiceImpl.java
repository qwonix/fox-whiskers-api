package ru.qwonix.foxwhiskersapi.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import ru.qwonix.foxwhiskersapi.entity.PickUpLocation;
import ru.qwonix.foxwhiskersapi.operation.FindPickUpLocation;
import ru.qwonix.foxwhiskersapi.repository.PickUpLocationRepository;
import ru.qwonix.foxwhiskersapi.service.PickUpLocationService;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class PickUpLocationServiceImpl implements PickUpLocationService {

    PickUpLocationRepository pickUpLocationRepository;

    @Override
    public List<PickUpLocation> getAll() {
        return pickUpLocationRepository.findAll();
    }

    @Override
    public FindPickUpLocation.Result getMaxPriority() {
        // descending order: 1 is higher priority than 2
        var optionalPickUpLocation = pickUpLocationRepository.findMinPriority();
        return prepare(optionalPickUpLocation);
    }

    @Override
    public FindPickUpLocation.Result getMinPriority() {
        // descending order: 2 is lower priority than 1
        var optionalPickUpLocation = pickUpLocationRepository.findMaxPriority();
        return prepare(optionalPickUpLocation);
    }

    @Override
    public FindPickUpLocation.Result getByPriority(Integer priority) {
        var optionalPickUpLocation = pickUpLocationRepository.findByPriority(priority);
        return prepare(optionalPickUpLocation);
    }

    private FindPickUpLocation.Result prepare(Optional<PickUpLocation> optionalPickUpLocation) {
        if (optionalPickUpLocation.isPresent()) {
            var pickUpLocation = optionalPickUpLocation.get();
            return FindPickUpLocation.Result.success(pickUpLocation);
        }
        return FindPickUpLocation.Result.notFound();
    }
}
