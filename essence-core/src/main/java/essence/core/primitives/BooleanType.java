package essence.core.primitives;

import essence.core.basic.DataType;
import essence.core.random.RandomGenerator;
import essence.core.validation.ValidationReporter;

public class BooleanType implements DataType<Boolean> {

    @Override
    public Boolean identity() {
        return false;
    }

    @Override
    public Boolean randomValue(RandomGenerator generator) {
        return generator.nextInt(0, 1) == 1;
    }

    @Override
    public void validate(Boolean value, ValidationReporter reporter) {
    }

}
