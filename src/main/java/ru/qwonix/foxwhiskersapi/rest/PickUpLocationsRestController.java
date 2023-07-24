package ru.qwonix.foxwhiskersapi.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.qwonix.foxwhiskersapi.entity.PickUpLocation;
import ru.qwonix.foxwhiskersapi.repository.PickUpLocationsRepository;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/location")
public class PickUpLocationsRestController {

    private final PickUpLocationsRepository pickUpLocationsRepository;

    public PickUpLocationsRestController(PickUpLocationsRepository pickUpLocationsRepository) {
        this.pickUpLocationsRepository = pickUpLocationsRepository;
    }

    @GetMapping()
    public ResponseEntity<List<PickUpLocation>> all() {
        return ResponseEntity.ok(pickUpLocationsRepository.findAll());
    }

    @GetMapping(params = "priority")
    public ResponseEntity<PickUpLocation> maxPriority(@RequestParam("priority") String priority) {
        if (priority.equalsIgnoreCase("max")) {
            return ResponseEntity.of(pickUpLocationsRepository.findFirstByOrderByPriorityDesc());
        }
        return ResponseEntity.of(pickUpLocationsRepository.findById(1L));
    }

}
