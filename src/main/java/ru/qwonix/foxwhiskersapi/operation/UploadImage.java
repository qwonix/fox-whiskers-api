package ru.qwonix.foxwhiskersapi.operation;

import lombok.Value;
import ru.qwonix.foxwhiskersapi.dto.ImageDataMetaInformation;
import ru.qwonix.foxwhiskersapi.dto.MenuItemDTO;
import ru.qwonix.foxwhiskersapi.entity.ImageData;

import java.util.List;

public record UploadImage() {

    public sealed interface Result {
        <T> T process(Processor<T> processor);


        static Result success(ImageDataMetaInformation imageDataMetaInformation) {
            return new Success(imageDataMetaInformation);
        }


        record Success(ImageDataMetaInformation imageDataMetaInformation) implements Result {

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSuccess(this);
            }
        }

        enum InvalidData implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processError(this);
            }
        }


        interface Processor<T> {
            T processSuccess(Success result);

            T processError(InvalidData result);
        }
    }
}
