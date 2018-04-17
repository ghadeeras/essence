package essence.core.primitives;

import essence.core.basic.DataType;
import essence.core.random.RandomGenerator;
import essence.core.validation.ValidationReporter;

public class IntegerType implements DataType<Integer> {

    @Override
    public Integer identity() {
        return 0;
    }

    @Override
    public Integer randomValue(RandomGenerator generator) {
        return generator.nextInt(Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    @Override
    public Integer closestTo(Integer value, ValidationReporter reporter) {
        return value;
    }

}
