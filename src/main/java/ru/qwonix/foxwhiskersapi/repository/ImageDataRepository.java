package ru.qwonix.foxwhiskersapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.qwonix.foxwhiskersapi.entity.ImageData;

import java.util.Optional;

@Repository
public interface ImageDataRepository extends JpaRepository<ImageData, Long> {
    Optional<ImageData> findByOriginalFileName(String originalFileName);

}
