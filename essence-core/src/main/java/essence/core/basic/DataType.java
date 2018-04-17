package essence.core.basic;

import essence.core.random.RandomGeneration;
import essence.core.random.RandomGenerator;
import essence.core.validation.ValidationReporter;

import java.util.Objects;

import static essence.core.validation.ValidationReporters.silent;

public interface DataType<T> {

    T identity();

    T randomValue(RandomGenerator generator);

    T closestTo(T value, ValidationReporter reporter);

    default T randomValue() {
        return randomValue(RandomGeneration.generator());
    }

    default T closestTo(T value) {
        return closestTo(value, silent);
    }

    default T closestToIdentity() {
        return closestTo(identity());
    }

    default boolean isValid(T value, ValidationReporter reporter) {
        return Objects.equals(value, closestTo(value, reporter));
    }

    default boolean isValid(T value) {
        return isValid(value, silent);
    }

    default String name() {
        return getClass().getSimpleName();
    }

}
