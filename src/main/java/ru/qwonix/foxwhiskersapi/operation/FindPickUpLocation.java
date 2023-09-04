package ru.qwonix.foxwhiskersapi.operation;

import lombok.Value;
import ru.qwonix.foxwhiskersapi.entity.Dish;
import ru.qwonix.foxwhiskersapi.entity.PickUpLocation;

import java.util.UUID;

public record FindPickUpLocation(UUID id) {

    public sealed interface Result {
        <T> T process(Processor<T> processor);

        static Result success(PickUpLocation pickUpLocation) {
            return new Success(pickUpLocation);
        }

        static Result notFound() {
            return NotFound.INSTANCE;
        }

        record Success(PickUpLocation pickUpLocation) implements Result {

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSuccess(this);
            }
        }

        enum NotFound implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processNotFound(this);
            }
        }

        interface Processor<T> {
            T processSuccess(Success result);

            T processNotFound(NotFound result);
        }

    }
}
