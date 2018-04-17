package essence.core.primitives;

import essence.core.basic.DataType;
import essence.core.random.RandomGenerator;
import essence.core.validation.ValidationReporter;

import java.util.function.Supplier;

import static essence.core.random.RandomGeneration.generator;

public class StringType implements DataType<String> {

    private static final int MAX_VARIATION = 16;

    @Override
    public String identity() {
        return "";
    }

    @Override
    public String randomValue(RandomGenerator generator) {
        Supplier<Character> characterSupplier = () -> generator().nextChar('A', (char) ('z' + 1));
        return generator.nextString(0, MAX_VARIATION, characterSupplier);
    }

    @Override
    public String closestTo(String value, ValidationReporter reporter) {
        return value;
    }

}
