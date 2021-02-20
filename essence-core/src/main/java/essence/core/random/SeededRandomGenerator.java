package essence.core.random;

import java.math.BigDecimal;
import java.util.Random;

import static java.math.BigDecimal.valueOf;

public class SeededRandomGenerator implements RandomGenerator {

    private final Random random;

    public SeededRandomGenerator(long seed) {
        random = new Random(seed);
    }

    @Override
    public int nextInt(int origin, int bound) {
        assert origin < bound;
        var result = (int) nextLong(origin, bound);
        assert result >= origin && result < bound;
        return result;
    }

    @Override
    public long nextLong(long origin, long bound) {
        assert origin < bound;
        var delta = bound - origin;
        var result = random.nextLong() % delta + origin;
        result = result < 0 ? result + delta : result;
        assert result >= origin && result < bound;
        return result;
    }

    @Override
    public BigDecimal nextDecimal(BigDecimal origin, BigDecimal bound) {
        assert origin.compareTo(bound) < 0;
        return bound
            .subtract(origin)
            .multiply(valueOf(random.nextDouble()))
            .add(origin);
    }

}
