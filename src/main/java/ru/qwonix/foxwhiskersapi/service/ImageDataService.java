package ru.qwonix.foxwhiskersapi.service;

import org.springframework.web.multipart.MultipartFile;
import ru.qwonix.foxwhiskersapi.operation.FindImage;
import ru.qwonix.foxwhiskersapi.operation.UploadImage;

import java.io.IOException;

public interface ImageDataService {

    UploadImage.Result uploadImage(MultipartFile file) throws IOException;

    FindImage.Result getImageByName(String imageName);
}
