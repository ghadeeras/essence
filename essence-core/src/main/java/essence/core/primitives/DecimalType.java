package essence.core.primitives;

import essence.core.ordinals.OrdinalType;
import essence.core.ordinals.Subset;
import essence.core.random.RandomGenerator;

import java.math.BigDecimal;

public class DecimalType extends OrdinalType<BigDecimal, BigDecimal, DecimalType> {

    DecimalType() {
        super(null);
    }

    private DecimalType(Subset<BigDecimal> subset) {
        super(subset);
    }

    @Override
    protected DecimalType create(Subset<BigDecimal> subset) {
        return new DecimalType(subset);
    }

    @Override
    public BigDecimal identity() {
        return BigDecimal.ZERO;
    }

    @Override
    protected BigDecimal min() {
        return max().negate();
    }

    @Override
    protected BigDecimal max() {
        return BigDecimal.valueOf(Double.MAX_VALUE);
    }

    @Override
    protected BigDecimal next(BigDecimal value) {
        return BigDecimal.valueOf(Math.nextUp(value.doubleValue()));
    }

    @Override
    protected BigDecimal prev(BigDecimal value) {
        return BigDecimal.valueOf(Math.nextDown(value.doubleValue()));
    }

    @Override
    protected BigDecimal add(BigDecimal value, BigDecimal distance) {
        return value.add(distance);
    }

    @Override
    protected BigDecimal randomDistance(RandomGenerator generator, BigDecimal range) {
        return generator.nextDecimal(BigDecimal.ZERO, range);
    }

    @Override
    protected BigDecimal distance(BigDecimal value1, BigDecimal value2) {
        return value1.subtract(value2).abs();
    }

}
