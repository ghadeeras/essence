package essence.core.validation;

import essence.core.basic.DataType;
import essence.core.utils.LazyValue;

import java.util.function.Supplier;

public class SimpleValidationIssue<T> implements ValidationIssue<T> {

    private final DataType<T> dataType;
    private final T value;
    private final LazyValue<String> message;

    public SimpleValidationIssue(DataType<T> dataType, T value, Supplier<String> message) {
        this.dataType = dataType;
        this.value = value;
        this.message = new LazyValue<>(message);
    }

    @Override
    public DataType<T> dataType() {
        return dataType;
    }

    @Override
    public T value() {
        return value;
    }

    @Override
    public String message() {
        return message.get();
    }

}
