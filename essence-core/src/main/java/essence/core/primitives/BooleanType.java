package essence.core.primitives;

import essence.core.basic.DataType;
import essence.core.random.RandomGenerator;
import essence.core.validation.ValidationReporter;

import java.util.Optional;

public class BooleanType implements DataType<Boolean> {

    private static final Integer ONE = 1;

    @Override
    public Class<Boolean> javaType() {
        return Boolean.class;
    }

    @Override
    public Optional<Boolean> arbitraryValue(RandomGenerator generator) {
        return generator.nextInt(0, 1).map(ONE::equals);
    }

    @Override
    public void validate(Boolean value, ValidationReporter reporter) {
    }

}
