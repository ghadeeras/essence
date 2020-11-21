package essence.core.basic;

import essence.core.random.RandomGenerator;
import essence.core.validation.SimpleValidationIssue;
import essence.core.validation.ValidationReporter;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    default T randomValue(RandomGenerator generator) {
        List<T> values = orderedValues();
        int index = generator.nextInt(0, values.size());
        return values.get(index);
    }

    @Override
    default void validate(T value, ValidationReporter reporter) {
        int comparison = orderedValues().stream()
            .mapToInt(v -> comparator().compare(v, value))
            .filter(i -> i >= 0)
            .findFirst()
            .orElse(1);
        if (comparison != 0) {
            reporter.report(new InvalidEnumerationValue<>(this, value));
        }
    }

}
