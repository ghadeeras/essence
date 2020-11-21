package essence.core.validation;

import essence.core.basic.DataType;

import java.util.function.Supplier;

public interface ValidationReporter {

    <T> void report(ValidationIssue<T> issue);

    default <T> void report(DataType<T> dataType, T value, Supplier<String> message) {
        report(new SimpleValidationIssue<>(dataType, value, message));
    }

}
