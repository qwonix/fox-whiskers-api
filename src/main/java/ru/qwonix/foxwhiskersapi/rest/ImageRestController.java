package ru.qwonix.foxwhiskersapi.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;
import ru.qwonix.foxwhiskersapi.entity.ImageData;
import ru.qwonix.foxwhiskersapi.service.ImageDataService;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/v1/image")
public class ImageRestController {

    private final ImageDataService imageDataService;

    public ImageRestController(ImageDataService imageDataService) {
        this.imageDataService = imageDataService;
    }


    @GetMapping("/{name}")
    public ResponseEntity<byte[]> getByName(@PathVariable("name") String name) {
        log.info("GET request name {}", name);
        Optional<ImageData> image = imageDataService.getImageByImageName(name);

        if (image.isPresent()) {
            ImageData imageData = image.get();
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(imageData.getMimeType()))
                    .body(imageData.getBytes());

        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("image") MultipartFile image,
                                    UriComponentsBuilder uriComponentsBuilder) throws IOException {
        ImageData imageData = imageDataService.uploadImage(image);

        return ResponseEntity.created(uriComponentsBuilder
                        .path("/api/v1/image/{name}")
                        .build(Map.of("name", imageData.getFileName())))
                .body(null);
    }
}
