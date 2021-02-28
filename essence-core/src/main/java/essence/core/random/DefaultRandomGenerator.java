package essence.core.random;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import static java.math.BigDecimal.valueOf;

public class DefaultRandomGenerator implements RandomGenerator {

    DefaultRandomGenerator() {
    }

    @Override
    public Optional<Integer> nextInt(int origin, int bound) {
        return origin < bound ?
            Optional.of(ThreadLocalRandom.current().nextInt(origin, bound)) :
            Optional.empty();
    }

    @Override
    public Optional<Long> nextLong(long origin, long bound) {
        return origin < bound ?
            Optional.of(ThreadLocalRandom.current().nextLong(origin, bound)) :
            Optional.empty();
    }

    @Override
    public Optional<BigDecimal> nextDecimal(BigDecimal origin, BigDecimal bound) {
        return origin.compareTo(bound) < 0 ?
            Optional.of(bound
                .subtract(origin)
                .multiply(valueOf(ThreadLocalRandom.current().nextDouble()))
                .add(origin)
            ) :
            Optional.empty();
    }

}
