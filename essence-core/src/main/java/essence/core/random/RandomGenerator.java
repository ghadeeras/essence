package essence.core.random;

import java.math.BigDecimal;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

public interface RandomGenerator {

    int nextInt(int origin, int bound);

    long nextLong(long origin, long bound);

    default char nextChar(char origin, char bound) {
        return (char) nextInt(origin, bound);
    }

    default String nextString(int minSize, int maxSize, Supplier<Character> chars) {
        int size = nextInt(minSize, maxSize + 1);
        return Stream.generate(chars).limit(size).map(Object::toString).collect(joining());
    }

    BigDecimal nextDecimal(BigDecimal origin, BigDecimal bound);

}
