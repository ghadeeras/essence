package essence.core.utils;

import java.util.function.BiFunction;
import java.util.stream.Stream;

public class StreamUtils {

    public static <T, R> R reduce(Stream<T> stream, R identity, BiFunction<R, ? super T, R> accumulator) {
        return stream.sequential().reduce(identity, accumulator, (a, b) -> b);
    }

}
