package ru.qwonix.foxwhiskersapi.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.MimeType;

import java.io.IOException;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageData implements JsonSerializable {

    private Long id;

    private String fileName;

    private String mimeType;

    @JsonIgnore
    private byte[] bytes;

    // json serialization into an identifying file name
    @Override
    public void serialize(JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(this.getFileName());
    }

    @Override
    public void serializeWithType(JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer) throws IOException {
        this.serialize(gen, serializers);
    }
}

