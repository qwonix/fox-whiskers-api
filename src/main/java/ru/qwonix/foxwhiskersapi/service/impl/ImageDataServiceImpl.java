package ru.qwonix.foxwhiskersapi.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.qwonix.foxwhiskersapi.dto.ImageDataMetaInformation;
import ru.qwonix.foxwhiskersapi.entity.ImageData;
import ru.qwonix.foxwhiskersapi.operation.FindImage;
import ru.qwonix.foxwhiskersapi.operation.UploadImage;
import ru.qwonix.foxwhiskersapi.repository.ImageDataRepository;
import ru.qwonix.foxwhiskersapi.service.ImageDataService;
import ru.qwonix.foxwhiskersapi.util.ImageUtil;

import java.io.IOException;
import java.net.URLConnection;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ImageDataServiceImpl implements ImageDataService {

    ImageDataRepository imageDataRepository;

    @Override
    public UploadImage.Result uploadImage(MultipartFile image) throws IOException {
        if (image.isEmpty() || image.getContentType() == null || !image.getContentType().startsWith("image/")) {
            return UploadImage.Result.InvalidData.INSTANCE;
        }
        var mimeType = URLConnection.guessContentTypeFromName(image.getOriginalFilename());
        var filename = UUID.randomUUID() + "_" + image.getOriginalFilename();

        var imageData = new ImageData();
        // TODO: 04-Sep-23 think about how you can more logically process the picture
        //  before working with the database. mb use getter setter
        imageData.setBytes(ImageUtil.compressImage(image.getBytes()));
        imageData.setMimeType(mimeType);
        imageData.setFileName(filename);

        // TODO: 04-Sep-23 replace with boolean response
        var insert = imageDataRepository.insert(imageData);

        return UploadImage.Result.success(new ImageDataMetaInformation(insert.getFileName(), insert.getMimeType()));
    }

    @Override
    public FindImage.Result getImageByName(String imageName) {
        var optionalImageData = imageDataRepository.findByImageName(imageName);
        if (optionalImageData.isPresent()) {
            var imageData = optionalImageData.get();
            byte[] bytes = ImageUtil.decompressImage(imageData.getBytes());
            imageData.setBytes(bytes);
            return FindImage.Result.success(imageData);
        }
        return FindImage.Result.notFound();
    }
}
