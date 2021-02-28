package essence.core.random;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

public interface RandomGenerator {

    Optional<Integer> nextInt(int origin, int bound);

    Optional<Long> nextLong(long origin, long bound);

    default Optional<Character> nextChar(char origin, char bound) {
        return nextInt(origin, bound).map(i -> (char) i.intValue());
    }

    default Optional<String> nextString(int minSize, int maxSize, Supplier<Optional<Character>> chars) {
        return nextInt(minSize, maxSize + 1).map(size -> Stream.generate(chars)
            .limit(size)
            .flatMap(Optional::stream)
            .map(Object::toString)
            .collect(joining())
        );
    }

    Optional<BigDecimal> nextDecimal(BigDecimal origin, BigDecimal bound);

}
