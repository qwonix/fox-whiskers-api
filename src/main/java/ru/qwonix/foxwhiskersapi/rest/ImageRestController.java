package ru.qwonix.foxwhiskersapi.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;
import ru.qwonix.foxwhiskersapi.entity.ImageData;
import ru.qwonix.foxwhiskersapi.service.impl.ImageDataServiceImpl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/v1/image")
public class ImageRestController {

    private final ImageDataServiceImpl imageDataServiceImpl;

    @Autowired
    public ImageRestController(ImageDataServiceImpl imageDataServiceImpl) {
        this.imageDataServiceImpl = imageDataServiceImpl;
    }

    @GetMapping("/{name}")
    public ResponseEntity<byte[]> getByName(@PathVariable("name") String name) {
        log.info("GET request name {}", name);
        Optional<ImageData> image = imageDataServiceImpl.getDecompressedImageByOriginalFileName(name);

        if (image.isPresent()) {
            ImageData imageData = image.get();
            return ResponseEntity.ok()
                    .contentType(MediaType.valueOf(imageData.getMimeType()))
                    .body(imageData.getBytes());

        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("image") MultipartFile file,
                                    UriComponentsBuilder uriComponentsBuilder) throws IOException {
        log.info("UPLOAD request {}", file.getOriginalFilename());

        ImageData imageData = imageDataServiceImpl.uploadImage(file);

        return ResponseEntity.created(uriComponentsBuilder
                .path("/api/v1/image/{name}")
                .build(new HashMap<String, String>() {{
                    put("name", imageData.getOriginalFileName());
                }})
        ).body(null);
    }
}
