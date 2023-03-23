package ru.qwonix.foxwhiskersapi.entity;

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

    @Column(name = "mimeType", nullable = false)
    private String mimeType;

    @Lob
    @Column(name = "bytes"/*columnDefinition = "LONGBLOB"*/)
    private byte[] bytes;
}
