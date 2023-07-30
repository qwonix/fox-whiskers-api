package ru.qwonix.foxwhiskersapi.service;

import org.springframework.web.multipart.MultipartFile;
import ru.qwonix.foxwhiskersapi.entity.ImageData;

import java.io.IOException;
import java.util.Optional;

public interface ImageDataService {

    ImageData uploadImage(MultipartFile file) throws IOException;

    Optional<ImageData> getImageByImageName(String imageName);
}
