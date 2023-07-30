package ru.qwonix.foxwhiskersapi.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.qwonix.foxwhiskersapi.entity.ImageData;
import ru.qwonix.foxwhiskersapi.repository.ImageDataRepository;
import ru.qwonix.foxwhiskersapi.service.ImageDataService;
import ru.qwonix.foxwhiskersapi.util.ImageUtil;

import java.io.IOException;
import java.net.URLConnection;
import java.util.Optional;

@Service
public class ImageDataServiceImpl implements ImageDataService {

    private final ImageDataRepository imageDataRepository;

    @Autowired
    public ImageDataServiceImpl(ImageDataRepository imageDataRepository) {
        this.imageDataRepository = imageDataRepository;
    }


    @Override
    public ImageData uploadImage(MultipartFile file) throws IOException {
        var originalFilename = file.getOriginalFilename();
        var mimeType = URLConnection.guessContentTypeFromName(originalFilename);

        var imageData = new ImageData();
        imageData.setBytes(ImageUtil.compressImage(file.getBytes()));
        imageData.setMimeType(mimeType);

        // FIXME: 30.07.2023 not sure that's the good way to fix the duplicate name problem
        var filename = originalFilename.substring(0, originalFilename.lastIndexOf("."));
        while (imageDataRepository.exists(filename)) {
            filename = filename + " - copy";
        }

        imageData.setFileName(filename);
        return imageDataRepository.insert(imageData);
    }

    @Override
    public Optional<ImageData> getImageByImageName(String imageName) {
        Optional<ImageData> optImageData = imageDataRepository.findByImageName(imageName);
        if (optImageData.isPresent()) {
            ImageData imageData = optImageData.get();
            byte[] bytes = ImageUtil.decompressImage(imageData.getBytes());
            imageData.setBytes(bytes);
        }
        return optImageData;
    }

    public Optional<ImageData> getDecompressedImageByImageName(String imageName) {
        Optional<ImageData> optImageData = getImageByImageName(imageName);
        if (optImageData.isPresent()) {
            ImageData imageData = optImageData.get();
            byte[] bytes = ImageUtil.decompressImage(imageData.getBytes());
            imageData.setBytes(bytes);
        }
        return optImageData;
    }
}
