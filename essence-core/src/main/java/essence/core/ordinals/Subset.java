package essence.core.ordinals;

import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Stream;

import static essence.core.utils.Equality.*;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

@SuppressWarnings({ "rawtypes", "EqualsWhichDoesntCheckParameterClass" })
public class Subset<T> {

    private final List<Limit<T>> limits;

    @SafeVarargs
    Subset(Limit<T>... limits) {
        this(Stream.of(limits).collect(toList()));
    }

    Subset(List<Limit<T>> limits) {
        this.limits = limits;
    }

    public List<Limit<T>> getLimits() {
        return limits;
    }

    @Override
    public String toString() {
        return limits.stream().map(Objects::toString).collect(joining(" "));
    }

    @Override
    public int hashCode() {
        return Objects.hash(limits.toArray());
    }

    private static final Function<Subset, List<Limit>> limitsGetter = Subset::getLimits;
    private static final BiPredicate<Subset, Object> equality = predicate(Subset.class, equalFor(limitsGetter, inSameOrder()));

    @Override
    public boolean equals(Object that) {
        return equality.test(this, that);
    }

}
