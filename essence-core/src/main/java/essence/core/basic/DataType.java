package essence.core.basic;

import essence.core.random.RandomGeneration;
import essence.core.random.RandomGenerator;
import essence.core.validation.ValidationReporter;
import essence.core.validation.ValidationReporterWrapper;

import static essence.core.validation.ValidationReporters.silent;
import static essence.core.validation.ValidationReporters.wrap;

public interface DataType<T> {

    T identity();

    T randomValue(RandomGenerator generator);

    void validate(T value, ValidationReporter reporter);

    default boolean isValid(T value, ValidationReporter reporter) {
        ValidationReporterWrapper wrapper = wrap(reporter);
        validate(value, wrapper);
        return wrapper.isValid();
    }

    default T randomValue() {
        return randomValue(RandomGeneration.generator());
    }

    default boolean isValid(T value) {
        return isValid(value, silent);
    }

    default String name() {
        return getClass().getSimpleName();
    }

}
