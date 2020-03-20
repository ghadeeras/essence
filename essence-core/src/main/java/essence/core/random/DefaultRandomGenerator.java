package essence.core.random;

import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;

import static java.math.BigDecimal.valueOf;

public class DefaultRandomGenerator implements RandomGenerator {

    DefaultRandomGenerator() {
    }

    @Override
    public int nextInt(int origin, int bound) {
        return ThreadLocalRandom.current().nextInt(origin, bound);
    }

    @Override
    public long nextLong(long origin, long bound) {
        return ThreadLocalRandom.current().nextLong(origin, bound);
    }

    @Override
    public BigDecimal nextDecimal(BigDecimal origin, BigDecimal bound) {
        return bound
            .subtract(origin)
            .multiply(valueOf(ThreadLocalRandom.current().nextDouble()))
            .add(origin);
    }

}
