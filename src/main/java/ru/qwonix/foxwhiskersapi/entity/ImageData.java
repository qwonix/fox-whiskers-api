package ru.qwonix.foxwhiskersapi.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "image_data")
public class ImageData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "original_file_name", nullable = false, unique = true)
    private String originalFileName;

    @Column(name = "mime_type", nullable = false)
    private String mimeType;

    @JsonIgnore
    @Lob
    @Column(name = "bytes")
    private byte[] bytes;
}
