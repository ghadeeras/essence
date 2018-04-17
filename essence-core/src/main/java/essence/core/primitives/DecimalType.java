package essence.core.primitives;

import essence.core.basic.DataType;
import essence.core.random.RandomGenerator;
import essence.core.validation.ValidationReporter;

import java.math.BigDecimal;

public class DecimalType implements DataType<BigDecimal> {

    @Override
    public BigDecimal identity() {
        return BigDecimal.ZERO;
    }

    @Override
    public BigDecimal randomValue(RandomGenerator generator) {
        return generator.nextDecimal(BigDecimal.ZERO, BigDecimal.ONE);
    }

    @Override
    public BigDecimal closestTo(BigDecimal value, ValidationReporter reporter) {
        return value;
    }

}
