package ru.qwonix.foxwhiskersapi.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.qwonix.foxwhiskersapi.entity.PickUpLocation;
import ru.qwonix.foxwhiskersapi.service.PickUpLocationService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/location")
public class PickUpLocationRestController {

    private final PickUpLocationService pickUpLocationService;

    public PickUpLocationRestController(PickUpLocationService pickUpLocationService) {
        this.pickUpLocationService = pickUpLocationService;
    }


    @GetMapping
    public ResponseEntity<List<PickUpLocation>> all() {
        return ResponseEntity.ok(pickUpLocationService.getAll());
    }

    @GetMapping(params = "priority")
    public ResponseEntity<?> priority(@RequestParam("priority") String priority) {
        return switch (priority.toLowerCase()) {
            case "max" -> ResponseEntity.of(pickUpLocationService.getMaxPriority());
            case "min" -> ResponseEntity.of(pickUpLocationService.getMinPriority());
            default -> {
                try {
                    yield ResponseEntity.of(pickUpLocationService.getByPriority(Integer.valueOf(priority)));
                } catch (NumberFormatException e) {
                    yield ResponseEntity.badRequest().body("can't be converted to int");
                }
            }
        };
    }

}
