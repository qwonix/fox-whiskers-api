package ru.qwonix.foxwhiskersapi.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;
import ru.qwonix.foxwhiskersapi.entity.ImageData;
import ru.qwonix.foxwhiskersapi.operation.FindImage;
import ru.qwonix.foxwhiskersapi.operation.UploadImage;
import ru.qwonix.foxwhiskersapi.service.ImageDataService;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping(ImageRestController.IMAGE_CONTROLLER_PATH)
public class ImageRestController {

    public static final String IMAGE_CONTROLLER_PATH = "/api/v1/image";
    private final ImageDataService imageDataService;

    public ImageRestController(ImageDataService imageDataService) {
        this.imageDataService = imageDataService;
    }


    @GetMapping("/{name}")
    public ResponseEntity<?> getByName(@PathVariable("name") String name) {
        return imageDataService.getImageByName(name).process(new FindImage.Result.Processor<>() {
            @Override
            public ResponseEntity<byte[]> processSuccess(FindImage.Result.Success result) {
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(result.imageData().getMimeType()))
                        .body(result.imageData().getBytes());
            }

            @Override
            public ResponseEntity<String> processNotFound(FindImage.Result.NotFound result) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No image with this name");
            }
        });

    }

    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("image") MultipartFile image,
                                    UriComponentsBuilder uriComponentsBuilder) throws IOException {
        return imageDataService.uploadImage(image).process(new UploadImage.Result.Processor<>() {
            @Override
            public ResponseEntity<String> processSuccess(UploadImage.Result.Success result) {
                return ResponseEntity.created(uriComponentsBuilder
                                .path(IMAGE_CONTROLLER_PATH + "/{name}")
                                .build(Map.of("name", result.imageDataMetaInformation().name())))
                        .body("success");
            }

            @Override
            public ResponseEntity<String> processError(UploadImage.Result.InvalidData result) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("processError");
            }
        });
    }
}
