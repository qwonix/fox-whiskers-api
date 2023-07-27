package ru.qwonix.foxwhiskersapi.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import lombok.Data;

import java.io.IOException;

@Data
public class ImageData implements JsonSerializable {
    public ImageData(String originalFileName, String mimeType, byte[] bytes) {
        this.originalFileName = originalFileName;
        this.mimeType = mimeType;
        this.bytes = bytes;
    }

    private Long id;

    private String originalFileName;

    private String mimeType;

    @JsonIgnore
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

