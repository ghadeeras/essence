package essence.examples.rest;

import java.util.function.Function;
import java.util.function.Supplier;

public class Profiling {

    public static <I, O> Function<I, O> profile(Function<I, O> function) {
        return input -> profile(() -> function.apply(input));
    }

    public static void profile(Runnable logic) {
        profile(() -> {
            logic.run();
            return null;
        });
    }

    public static <T> T profile(Supplier<T> logic) {
        long time = System.currentTimeMillis();
        try {
            return logic.get();
        } finally {
            System.out.println("Time = " + (System.currentTimeMillis() - time));
        }
    }

}
