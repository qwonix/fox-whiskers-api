package ru.qwonix.foxwhiskersapi.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.qwonix.foxwhiskersapi.entity.PickUpLocation;
import ru.qwonix.foxwhiskersapi.operation.FindPickUpLocation;
import ru.qwonix.foxwhiskersapi.service.PickUpLocationService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/location")
public class PickUpLocationRestController {

    private final PickUpLocationService pickUpLocationService;

    private final FindPickUpLocation.Result.Processor<ResponseEntity<?>> processor = new FindPickUpLocation.Result.Processor<>() {
        @Override
        public ResponseEntity<?> processSuccess(FindPickUpLocation.Result.Success result) {
            return ResponseEntity.ok(result.pickUpLocation());
        }

        @Override
        public ResponseEntity<?> processNotFound(FindPickUpLocation.Result.NotFound result) {
            return ResponseEntity.notFound().build();

        }
    };

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
            case "max" -> pickUpLocationService.getMaxPriority().process(processor);
            case "min" -> pickUpLocationService.getMinPriority().process(processor);
            default -> {
                try {
                    yield pickUpLocationService.getByPriority(Integer.valueOf(priority)).process(processor);
                } catch (NumberFormatException e) {
                    yield ResponseEntity.badRequest().body("can't be converted to int");
                }
            }
        };
    }

}
