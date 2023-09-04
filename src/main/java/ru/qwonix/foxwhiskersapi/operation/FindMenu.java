package ru.qwonix.foxwhiskersapi.operation;

import lombok.Value;
import ru.qwonix.foxwhiskersapi.dto.MenuItemDTO;
import ru.qwonix.foxwhiskersapi.entity.Dish;

import java.util.List;
import java.util.UUID;

public record FindMenu() {

    public sealed interface Result {
        <T> T process(Processor<T> processor);

        static Result success(List<MenuItemDTO> menuItems) {
            return new Success(menuItems);
        }


        record Success(List<MenuItemDTO> menuItems) implements Result {

            @Override
            public <T> T process(Processor<T> processor) {
                return processor.processSuccess(this);
            }
        }


        interface Processor<T> {
            T processSuccess(Success result);
        }
    }
}
