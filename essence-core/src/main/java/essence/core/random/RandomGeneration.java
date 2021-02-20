package essence.core.random;

import java.util.function.Supplier;

import static java.lang.ThreadLocal.withInitial;

public class RandomGeneration {

    private RandomGeneration() {
    }

    public static final RandomGenerator defaultGenerator = new DefaultRandomGenerator();

    private static final ThreadLocal<RandomGenerator> current = withInitial(() -> defaultGenerator);

    public static <T> T using(RandomGenerator generator, Supplier<T> logic) {
        var oldGenerator = generator();
        current.set(generator);
        try {
            return logic.get();
        } finally {
            current.set(oldGenerator);
        }
    }

    public static void using(RandomGenerator generator, Runnable logic) {
        using(generator, () -> {
            logic.run();
            return null;
        });
    }

    public static RandomGenerator generator() {
        return current.get();
    }

}
