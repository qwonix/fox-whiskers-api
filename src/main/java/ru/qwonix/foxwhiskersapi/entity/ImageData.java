package ru.qwonix.foxwhiskersapi.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import lombok.*;

import javax.persistence.*;
import java.io.IOException;


@Data
@ToString(exclude = "bytes")
@Builder
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "image_data")
public class ImageData implements JsonSerializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "original_file_name", nullable = false, unique = true)
    private String originalFileName;

    @Column(name = "mime_type", nullable = false)
    private String mimeType;

    @JsonIgnore
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "bytes")
    private byte[] bytes;

    // json serialization into an identifying file name
    @Override
    public void serialize(JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(this.getOriginalFileName());
    }

    @Override
    public void serializeWithType(JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer) throws IOException {
        this.serialize(gen, serializers);
    }
}

