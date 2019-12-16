package essence.core.ordinals;

import java.util.Comparator;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

import static essence.core.utils.Equality.*;
import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;

public class Limit<T> {

    private final T value;
    private final boolean lower;
    private final boolean inclusive;

    private final int order;

    public Limit(T value, boolean lower, boolean inclusive) {
        this.value = value;
        this.lower = lower;
        this.inclusive = inclusive;
        this.order = integer(lower ^ inclusive, lower) ;
    }

    private static int integer(Boolean... bits) {
        return Stream.of(bits).reduce(0, (i, b) -> (i << 1) | (b ? 1 : 0), (i1, i2) -> i1 | i2);
    }

    private int getOrder() {
        return order;
    }

    public Limit<T> complement() {
        return new Limit<>(value, !lower, !inclusive);
    }

    public T getValue() {
        return value;
    }

    public boolean isLower() {
        return lower;
    }

    public boolean isUpper() {
        return !lower;
    }

    public boolean isInclusive() {
        return inclusive;
    }

    public boolean isExclusive() {
        return !inclusive;
    }

    @Override
    public String toString() {
        return lower ?
            inclusive ? "[" + value + " .." : value + "[ .." :
            inclusive ? ".. " + value + "]" : ".. ]" + value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, order);
    }

    private static BiPredicate<Limit, Object> equality = predicate(Limit.class, when(
        equalFor(Limit::getValue),
        equalFor(Limit::getOrder)
    ));

    @Override
    public boolean equals(Object that) {
        return equality.test(this, that);
    }

    public static <T> Comparator<Limit<T>> comparator(Comparator<T> comparator) {
        Comparator<Limit<T>> valueComparator = comparing(Limit::getValue, comparator);
        Comparator<Limit<T>> orderComparator = comparing(Limit::getOrder, naturalOrder());
        return valueComparator.thenComparing(orderComparator);
    }

}
