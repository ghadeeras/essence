package essence.core.primitives;

import essence.core.basic.DataType;
import essence.core.random.RandomGenerator;
import essence.core.validation.ValidationReporter;

public class LongType implements DataType<Long> {

    @Override
    public Long identity() {
        return 0L;
    }

    @Override
    public Long randomValue(RandomGenerator generator) {
        return (long) generator.nextInt(Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    @Override
    public Long closestTo(Long value, ValidationReporter reporter) {
        return value;
    }

}
