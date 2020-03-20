package essence.core.ordinals;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class SubsetOperations<T> {

    private final OrdinalType<T, ?> ordinalType;
    private final Limit<T> minLimit;
    private final Limit<T> maxLimit;
    private final Subset<T> all;
    private final Subset<T> empty;
    private final Comparator<Limit<T>> limitComparator;

    protected SubsetOperations(OrdinalType<T, ?> ordinalType) {
        this.ordinalType = ordinalType;
        this.minLimit = new Limit<>(ordinalType.min(), true, true);
        this.maxLimit = new Limit<>(ordinalType.max(), false, true);
        this.all = new Subset<>(minLimit, maxLimit);
        this.empty = new Subset<>();
        this.limitComparator = Limit.comparator(ordinalType.comparator());
    }

    public Subset<T> except(T value) {
        return complement(only(value));
    }

    public Subset<T> only(T value) {
        DirectionlessLimit limit = including(value);
        return range(limit, limit);
    }

    public Subset<T> range(DirectionlessLimit from, DirectionlessLimit to) {
        return intersection(above(from), below(to));
    }

    public Subset<T> below(DirectionlessLimit limit) {
        return subset(limit.upper());
    }

    public Subset<T> above(DirectionlessLimit limit) {
        return subset(limit.lower());
    }

    public DirectionlessLimit including(T value) {
        return new DirectionlessLimit(isLower -> new Limit<>(value, isLower, true));
    }

    public DirectionlessLimit excluding(T value) {
        return new DirectionlessLimit(isLower -> new Limit<>(value, isLower, false));
    }

    @SafeVarargs
    public final Subset<T> union(Subset<T>... subsets) {
        return union(listOf(subsets));
    }

    public Subset<T> union(List<Subset<T>> subsets) {
        long nestingLevel = subsets.stream()
            .filter(subset -> !subset.getLimits().isEmpty())
            .filter(subset -> !subset.getLimits().get(0).isLower())
            .count();

        List<Limit<T>> limits = subsets.stream()
            .map(Subset::getLimits)
            .flatMap(List::stream)
            .sorted(limitComparator)
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

        return unionLimits.size() > 0 || nestingLevel == 0 ? subset(unionLimits) : all;
    }

    public Subset<T> complement(Subset<T> subset) {
        List<Limit<T>> limits = subset.getLimits().stream()
            .map(Limit::complement)
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

    @SafeVarargs
    private final Subset<T> subset(Limit<T>... limits) {
        return subset(listOf(limits));
    }

    private Subset<T> subset(List<Limit<T>> limits) {
        List<Limit<T>> filteredLimits = limits.stream()
            .filter(includes(ordinalType.min()).or(includes(ordinalType.max())))
            .collect(toList());
        Subset<T> result = empty;
        if (!filteredLimits.isEmpty()) {
            filteredLimits.removeIf(includes(ordinalType.min()).and(includes(ordinalType.max())));
            result = !filteredLimits.isEmpty() ? new Subset<>(filteredLimits) : all;
        }
        return result;
    }

    private Predicate<Limit<T>> includes(T value) {
        return limit -> {
            int c = ordinalType.comparator().compare(value, limit.getValue());
            return (limit.isInclusive() ? c >= 0 : c > 0) ^ limit.isUpper();
        };
    }

    @SafeVarargs
    private static <T> List<T> listOf(T... values) {
        return Stream.of(values).collect(toList());
    }

    private LinkedList<Subset<T>> mergeLists(LinkedList<Subset<T>> subsets1, LinkedList<Subset<T>> subsets2) {
        subsets1.addAll(subsets2);
        return subsets1;
    }

    public class DirectionlessLimit {

        private final Function<Boolean, Limit<T>> limitConstructor;

        private DirectionlessLimit(Function<Boolean, Limit<T>> limitConstructor) {
            this.limitConstructor = limitConstructor;
        }

        private Limit<T> lower() {
            return limitConstructor.apply(true);
        }

        private Limit<T> upper() {
            return limitConstructor.apply(false);
        }

        public Subset<T> to(DirectionlessLimit upperLimit) {
            return range(this, upperLimit);
        }

        public Subset<T> from(DirectionlessLimit lowerLimit) {
            return range(lowerLimit, this);
        }

        public Subset<T> andAbove() {
            return above(this);
        }

        public Subset<T> andBelow() {
            return below(this);
        }

    }

}
