package ru.qwonix.foxwhiskersapi.operation;

import lombok.Value;
import ru.qwonix.foxwhiskersapi.entity.Dish;
import ru.qwonix.foxwhiskersapi.entity.Order;

import java.util.UUID;

public record CreateOrder(UUID id) {

    public sealed interface Result {
        <T> T process(Processor<T> processor);

        static Result success(Order order) {
            return new Success(order);
        }

        static Result userNotFound() {
            return UserNotFound.INSTANCE;
        }

        static Result pickUpLocationNotFound() {
            return PickUpLocationNotFound.INSTANCE;
        }

        record Success(Order order) implements Result {

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSuccess(this);
            }
        }

        enum UserNotFound implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processUserNotFound(this);
            }
        }

        enum PickUpLocationNotFound implements Result {
            INSTANCE;

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processPickUpLocationNotFound(this);
            }
        }

        interface Processor<T> {
            T processSuccess(Success result);

            T processUserNotFound(UserNotFound result);

            T processPickUpLocationNotFound(PickUpLocationNotFound result);
        }

    }
}
