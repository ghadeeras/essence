package essence.core.random;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Random;

import static java.math.BigDecimal.valueOf;

public class SeededRandomGenerator implements RandomGenerator {

    private final Random random;

    public SeededRandomGenerator(long seed) {
        random = new Random(seed);
    }

    @Override
    public Optional<Integer> nextInt(int origin, int bound) {
        return nextLong(origin, bound).map(Long::intValue);
    }

    @Override
    public Optional<Long> nextLong(long origin, long bound) {
        if (origin < bound) {
            var delta = bound - origin;
            var result = random.nextLong() % delta + origin;
            result = result < 0 ? result + delta : result;
            assert result >= origin && result < bound;
            return Optional.of(result);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<BigDecimal> nextDecimal(BigDecimal origin, BigDecimal bound) {
        assert origin.compareTo(bound) < 0;
        return origin.compareTo(bound) < 0 ?
            Optional.of(bound
                .subtract(origin)
                .multiply(valueOf(random.nextDouble()))
                .add(origin)
            ) :
            Optional.empty();
    }

}
