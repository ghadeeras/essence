package essence.core.basic;

import essence.core.random.RandomGeneration;
import essence.core.random.RandomGenerator;
import essence.core.validation.ValidationReporter;

import java.util.Optional;

import static essence.core.validation.ValidationReporters.silent;
import static essence.core.validation.ValidationReporters.wrap;

public interface DataType<T> {

    Class<T> javaType();

    Optional<T> arbitraryValue(RandomGenerator generator);

    void validate(T value, ValidationReporter reporter);

    default Optional<T> arbitraryValue() {
        return arbitraryValue(RandomGeneration.generator());
    }

    default T randomValue(RandomGenerator generator) {
        return arbitraryValue(generator).orElseThrow();
    }

    default T randomValue() {
        return randomValue(RandomGeneration.generator());
    }

    default boolean isEmpty() {
        return arbitraryValue().isEmpty();
    }

    default boolean isValid(T value, ValidationReporter reporter) {
        var wrapper = wrap(reporter);
        validate(value, wrapper);
        return wrapper.isValid();
    }

    default boolean isValid(T value) {
        return isValid(value, silent);
    }

    default String name() {
        return getClass().getSimpleName();
    }

}
