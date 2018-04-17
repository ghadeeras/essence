package essence.core.basic;

import essence.core.random.RandomGenerator;
import essence.core.validation.SimpleValidationIssue;
import essence.core.validation.ValidationReporter;

import java.util.*;

import static essence.core.utils.ProximityUtils.inOrderedValues;

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
    default T closestTo(T value, ValidationReporter reporter) {
        T result = inOrderedValues(orderedValues(), comparator()).findClosestTo(value);
        if (!Objects.equals(value, result)) {
            reporter.report(new InvalidEnumerationValue<>(this, value));
        }
        return result;
    }

}
