package essence.core.basic;

import essence.core.random.RandomGenerator;
import essence.core.validation.SimpleValidationIssue;
import essence.core.validation.ValidationReporter;

import java.util.*;

public interface EnumerableType<T> extends DataType<T> {

    class InvalidEnumerationValue<T> extends SimpleValidationIssue<T> {

        private InvalidEnumerationValue(DataType<T> dataType, T value) {
            super(dataType, value, () -> "Invalid " + dataType.name() + " value: " + value);
        }

    }

    List<T> orderedValues();

    default Set<T> values() {
        return new HashSet<>(orderedValues());
    }

    default Comparator<T> comparator() {
        return Comparator.comparingInt(orderedValues()::indexOf);
    }

    @Override
    default Optional<T> arbitraryValue(RandomGenerator generator) {
        var values = orderedValues();
        return generator.nextInt(0, values.size()).map(values::get);
    }

    @Override
    default void validate(T value, ValidationReporter reporter) {
        var comparison = orderedValues().stream()
            .mapToInt(v -> comparator().compare(v, value))
            .filter(i -> i >= 0)
            .findFirst()
            .orElse(1);
        if (comparison != 0) {
            reporter.report(new InvalidEnumerationValue<>(this, value));
        }
    }

}
