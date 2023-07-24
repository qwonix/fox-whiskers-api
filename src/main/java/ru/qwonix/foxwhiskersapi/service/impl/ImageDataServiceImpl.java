package ru.qwonix.foxwhiskersapi.service.impl;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.qwonix.foxwhiskersapi.entity.ImageData;
import ru.qwonix.foxwhiskersapi.repository.ImageDataRepository;
import ru.qwonix.foxwhiskersapi.util.ImageUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@Service
public class ImageDataServiceImpl {

    private final ImageDataRepository imageDataRepository;

    @Autowired
    public ImageDataServiceImpl(ImageDataRepository imageDataRepository) {
        this.imageDataRepository = imageDataRepository;
    }

    @Transactional
    public ImageData uploadImage(ImageData imageData) {
        return imageDataRepository.save(imageData);
    }

    @Transactional
    public ImageData uploadImage(Path path) throws IOException {
        String fileName = path.getFileName().toString();
        ImageData imageData = ImageData.builder()
                .originalFileName(fileName)
                .mimeType(fileName.substring(fileName.lastIndexOf(".") + 1))
                .bytes(ImageUtil.compressImage(Files.readAllBytes(path)))
                .build();

        return uploadImage(imageData);
    }

    @Transactional
    public ImageData uploadImage(MultipartFile file) throws IOException {
        ImageData imageData = ImageData.builder()
                .originalFileName(file.getOriginalFilename())
                .bytes(ImageUtil.compressImage(file.getBytes())).build();

        return uploadImage(imageData);
    }

    @Transactional
    public Optional<ImageData> getImageByOriginalFileName(String originalFileName) {
        return imageDataRepository.findByOriginalFileName(originalFileName);
    }

    public Optional<ImageData> getDecompressedImageByOriginalFileName(String originalFileName) {
        Optional<ImageData> optImageData = getImageByOriginalFileName(originalFileName);
        if (optImageData.isPresent()) {
            ImageData imageData = optImageData.get();
            byte[] bytes = ImageUtil.decompressImage(imageData.getBytes());
            imageData.setBytes(bytes);
        }
        return optImageData;
    }
}
