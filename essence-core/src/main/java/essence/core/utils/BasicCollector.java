package essence.core.utils;

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import static java.util.Collections.emptySet;
import static java.util.function.Function.identity;

public class BasicCollector<T, A, R> implements Collector<T, A, R> {

    private final Supplier<A> supplier;
    private final BiConsumer<A, T> accumulator;
    private final BinaryOperator<A> combiner;
    private final Function<A, R> finisher;

    public BasicCollector(Supplier<A> supplier, BiConsumer<A, T> accumulator, BinaryOperator<A> combiner, Function<A, R> finisher) {
        this.supplier = supplier;
        this.accumulator = accumulator;
        this.combiner = combiner;
        this.finisher = finisher;
    }

    @Override
    public Supplier<A> supplier() {
        return supplier;
    }

    @Override
    public BiConsumer<A, T> accumulator() {
        return accumulator;
    }

    @Override
    public BinaryOperator<A> combiner() {
        return combiner;
    }

    @Override
    public Function<A, R> finisher() {
        return finisher;
    }

    @Override
    public Set<Characteristics> characteristics() {
        return emptySet();
    }

    public static <T, A, R> Collector<T, A, R> collector(Supplier<A> supplier, BiConsumer<A, T> accumulator, BinaryOperator<A> combiner, Function<A, R> finisher) {
        return new BasicCollector<>(supplier, accumulator, combiner, finisher);
    }

    public static <T, A, R> Collector<T, A, R> collector(Supplier<A> supplier, BiConsumer<A, T> accumulator, Function<A, R> finisher) {
        return collector(supplier, accumulator, (a1, a2) -> a2, finisher);
    }

    public static <T, R> Collector<T, R, R> collector(Supplier<R> supplier, BiConsumer<R, T> accumulator, BinaryOperator<R> combiner) {
        return collector(supplier, accumulator, combiner, identity());
    }

    public static <T, R> Collector<T, R, R> collector(Supplier<R> supplier, BiConsumer<R, T> accumulator) {
        return collector(supplier, accumulator, (r1, r2) -> r2, identity());
    }

}
