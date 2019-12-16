package essence.core.ordinals;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class SubsetOperations<T> {

    private final OrdinalType<T> type;
    private final Subset<T> empty;
    private final Subset<T> all;

    public SubsetOperations(OrdinalType<T> type) {
        this.type = type;
        this.empty = new Subset<>();
        this.all = new Subset<>(
            new Limit<>(type.min(), true, true),
            new Limit<>(type.max(), false, true)
        );
    }

    @SafeVarargs
    private static <T> List<T> listOf(T... values) {
        return Stream.of(values).collect(toList());
    }

    @SafeVarargs
    private final Subset<T> subset(Limit<T>... limits) {
        return subset(listOf(limits));
    }

    private Subset<T> subset(List<Limit<T>> limits) {
        List<Limit<T>> filteredLimits = limits.stream()
            .filter(includes(type.min()).or(includes(type.max())))
            .collect(toList());
        Subset<T> result = empty();
        if (!filteredLimits.isEmpty()) {
            filteredLimits.removeIf(includes(type.min()).and(includes(type.max())));
            result = !filteredLimits.isEmpty() ? new Subset<>(filteredLimits) : all();
        }
        return result;
    }

    private Predicate<Limit<T>> includes(T value) {
        return limit -> {
            int c = type.comparator().compare(value, limit.getValue());
            return (limit.isInclusive() ? c >= 0 : c > 0) ^ limit.isUpper();
        };
    }

    private Comparator<Limit<T>> limitComparator() {
        return Limit.comparator(type.comparator());
    }

    public LimitSupplier<T> including(T value) {
        return lower -> new Limit<>(value, lower, true);
    }

    public LimitSupplier<T> excluding(T value) {
        return lower -> new Limit<>(value, lower, false);
    }

    public Subset<T> below(LimitSupplier<T> to) {
        return subset(to.create(false));
    }

    public Subset<T> above(LimitSupplier<T> from) {
        return subset(from.create(true));
    }

    public Subset<T> range(LimitSupplier<T> from, LimitSupplier<T> to) {
        return intersection(above(from), below(to));
    }

    public Subset<T> only(T value) {
        LimitSupplier<T> limit = including(value);
        return range(limit, limit);
    }

    public Subset<T> except(T value) {
        return complement(only(value));
    }

    public Subset<T> empty() {
        return empty;
    }

    public Subset<T> all() {
        return all;
    }

    @SafeVarargs
    public final Subset<T> union(Subset<T>... subsets) {
        return union(listOf(subsets));
    }

    public Subset<T> union(List<Subset<T>> subsets) {
        long nestingLevel = subsets.stream()
            .filter(subset -> !subset.getLimits().isEmpty())
            .filter(subset -> !subset.getLimits().get(0).isUpper())
            .count();

        List<Limit<T>> limits = subsets.stream()
            .map(Subset::getLimits)
            .flatMap(List::stream)
            .sorted(limitComparator())
            .collect(toList());

        LinkedList<Limit<T>> unionLimits = new LinkedList<>();
        Consumer<Limit<T>> appender = limit -> {
            if (!unionLimits.isEmpty() && unionLimits.getLast().equals(limit.complement())) {
                unionLimits.removeLast();
            } else {
                unionLimits.add(limit);
            }
        };

        for (Limit<T> limit : limits) {
            if (limit.isLower()) {
                if (nestingLevel == 0) {
                    appender.accept(limit);
                }
                nestingLevel++;
            } else {
                nestingLevel--;
                if (nestingLevel == 0) {
                    unionLimits.add(limit);
                }
            }
        }

        return unionLimits.size() > 0 || nestingLevel == 0 ? subset(unionLimits) : all();
    }

    public Subset<T> complement(Subset<T> subset) {
        List<Limit<T>> limits = subset.getLimits().stream()
            .map(Limit::complement)
            .sorted(limitComparator())
            .collect(toList());
        return subset(limits);
    }

    @SafeVarargs
    public final Subset<T> intersection(Subset<T>... subsets) {
        return intersection(listOf(subsets));
    }

    public Subset<T> intersection(List<Subset<T>> subsets) {
        List<Subset<T>> complements = subsets.stream().map(this::complement).collect(toList());
        return complement(union(complements));
    }

    public interface LimitSupplier<T> {

        Limit<T> create(boolean lower);

    }

}
