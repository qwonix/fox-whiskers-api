package ru.qwonix.foxwhiskersapi.repository;


import ru.qwonix.foxwhiskersapi.entity.ImageData;

import java.util.Optional;

public interface ImageDataRepository extends CrudRepository<ImageData, String> {
    Optional<ImageData> findByImageName(String imageName);

    Boolean exists(String imageName);

}
