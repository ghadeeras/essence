package essence.core.primitives;

import essence.core.ordinals.OrdinalType;
import essence.core.ordinals.Subset;
import essence.core.random.RandomGenerator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

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
    protected BigDecimal min() {
        return max().negate();
    }

    @Override
    protected BigDecimal max() {
        return BigDecimal.TEN.pow(9);
    }

    @Override
    protected BigDecimal next(BigDecimal value) {
        return value.add(BigDecimal.valueOf(0.1).pow(9));
    }

    @Override
    protected BigDecimal prev(BigDecimal value) {
        return value.subtract(BigDecimal.valueOf(0.1).pow(9));
    }

    @Override
    protected BigDecimal add(BigDecimal value, BigDecimal distance) {
        return value.add(distance);
    }

    @Override
    protected Optional<BigDecimal> randomDistance(RandomGenerator generator, BigDecimal range) {
        return generator.nextDecimal(BigDecimal.ZERO, range).map(d -> d.setScale(9, RoundingMode.HALF_EVEN));
    }

    @Override
    protected BigDecimal distance(BigDecimal value1, BigDecimal value2) {
        return value1.subtract(value2).abs();
    }

}
