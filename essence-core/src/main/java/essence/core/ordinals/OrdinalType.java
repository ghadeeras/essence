package essence.core.ordinals;

import essence.core.basic.DataType;
import essence.core.random.RandomGenerator;
import essence.core.utils.LazyValue;
import essence.core.validation.ValidationReporter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static essence.core.ordinals.Limit.*;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

public abstract class OrdinalType<T, D extends Comparable<D>, O extends  OrdinalType<T, D, O>> implements DataType<T> {

    final LazyValue<List<Range>> ranges = LazyValue.from(this::inclusiveRanges);

    final Subset<T> subset;
    final Subset<T> all;
    final Subset<T> empty;
    final Comparator<Limit<T>> limitComparator;

    protected OrdinalType(Subset<T> subset) {
        var minLimit = new Limit<>(safeMin(), true, true);
        var maxLimit = new Limit<>(safeMax(), false, true);
        this.subset = subset != null ? subset : new Subset<>(minLimit, maxLimit);
        this.all = subset(minLimit, maxLimit);
        this.empty = subset();
        this.limitComparator = Limit.comparator(comparator());
    }

    public O only(T value) {
        return constraint(subset(fromIncluding(value), toIncluding(value)));
    }

    public O except(T value) {
        return constraint(subset(toExcluding(value), fromExcluding(value)));
    }

    public O in(T from, T to) {
        return comparator().compare(from, to) <= 0 ?
            in(fromIncluding(from), toExcluding(to)) :
            in(fromExcluding(to), toIncluding(from));
    }

    public O in(Limit<T> from, Limit<T> to) {
        return constraint(subset(from, to));
    }

    public O greaterThanOrEqualTo(T value) {
        return constraint(subset(fromIncluding(value)));
    }

    public O greaterThan(T value) {
        return constraint(subset(fromExcluding(value)));
    }

    public O lessThanOrEqualTo(T value) {
        return constraint(subset(toIncluding(value)));
    }

    public O lessThan(T value) {
        return constraint(subset(toExcluding(value)));
    }

    @SafeVarargs
    public final O union(O... types) {
        return create(union(subsets(types)));
    }

    @SafeVarargs
    public final O intersect(O... types) {
        return create(intersection(subsets(types)));
    }

    public final O complement() {
        return create(complement(subset));
    }

    protected O constraint(Subset<T> subset) {
        return intersect(create(subset));
    }

    protected abstract O create(Subset<T> subset);

    private Subset<T> intersection(List<Subset<T>> subsets) {
        var complements = subsets.stream().map(this::complement).collect(toList());
        return complement(union(complements));
    }

    private Subset<T> union(List<Subset<T>> subsets) {
        var nestingLevel = subsets.stream()
            .filter(subset -> !subset.getLimits().isEmpty())
            .filter(subset -> !subset.getLimits().get(0).isLower())
            .count();

        var limits = subsets.stream()
            .map(Subset::getLimits)
            .flatMap(List::stream)
            .sorted(limitComparator)
            .collect(toList());

        var unionLimits = new LinkedList<Limit<T>>();
        Consumer<Limit<T>> appender = limit -> {
            if (!unionLimits.isEmpty() && unionLimits.getLast().equals(limit.complement())) {
                unionLimits.removeLast();
            } else {
                unionLimits.add(limit);
            }
        };

        for (var limit : limits) {
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

    private Subset<T> complement(Subset<T> subset) {
        var limits = subset.getLimits().stream()
            .map(Limit::complement)
            .collect(toList());
        return subset(limits);
    }

    @SafeVarargs
    private List<Subset<T>> subsets(O... types) {
        return Stream.concat(
            Stream.of(this),
            Stream.of(types)
        ).map(t -> t.subset).collect(toList());
    }

    @SafeVarargs
    private Subset<T> subset(Limit<T>... limits) {
        return subset(Stream.of(limits).collect(toList()));
    }

    private Subset<T> subset(List<Limit<T>> limits) {
        var filteredLimits = limits.stream()
            .filter(includes(safeMin()).or(includes(safeMax())))
            .collect(toList());
        var result = new Subset<T>();
        if (!filteredLimits.isEmpty()) {
            filteredLimits.removeIf(includes(safeMin()).and(includes(safeMax())));
            result = !filteredLimits.isEmpty() ? new Subset<>(filteredLimits) : all;
        }
        return result;
    }

    private Predicate<Limit<T>> includes(T value) {
        return limit -> {
            var c = comparator().compare(value, limit.getValue());
            return (limit.isInclusive() ? c >= 0 : c > 0) ^ limit.isUpper();
        };
    }

    @Override
    public T randomValue(RandomGenerator generator) {
        var rangeIndex = generator.nextInt(0, ranges().size());
        var range = ranges().get(rangeIndex);
        return add(range.min, randomDistance(generator, distance(range.min, next(range.max))));
    }

    @Override
    public void validate(T value, ValidationReporter reporter) {
        if (ranges().stream().noneMatch(range -> range.contains(value))) {
            reporter.report(this, value, () -> value + " is not in " + subset);
        }
    }

    private List<Range> ranges() {
        return ranges.get();
    }

    private T safeMin() {
        return next(min());
    }

    private T safeMax() {
        return prev(max());
    }

    protected Comparator<T> comparator() {
        return Comparator.comparing(o -> distance(safeMin(), o));
    }

    protected abstract T min();

    protected abstract T max();

    protected abstract T next(T value);

    protected abstract T prev(T value);

    protected abstract T add(T value, D distance);

    protected abstract D randomDistance(RandomGenerator generator, D range);

    protected abstract D distance(T value1, T value2);

    private List<Range> inclusiveRanges() {
        return subset != null ?
            new ArrayList<>(subset.getLimits().stream()
                .map(this::toInclusive)
                .reduce(new LinkedList<>(), this::addToRanges, OrdinalType::mergeLists)) :
            singletonList(new Range(safeMin(), safeMax()));
    }

    private Limit<T> toInclusive(Limit<T> limit) {
        var value = limit.getValue();
        return limit.isExclusive() ?
            limit.isLower() ?
                new Limit<>(next(value), true, true) :
                new Limit<>(prev(value), false, true) :
            limit;
    }

    private LinkedList<Range> addToRanges(LinkedList<Range> ranges, Limit<T> limit) {
        ranges.add(limit.isLower() ?
            new Range(limit.getValue(), safeMax()) :
            ranges.isEmpty() ?
                new Range(safeMin(), limit.getValue()) :
                new Range(ranges.removeLast().min, limit.getValue())
        );
        return ranges;
    }

    private static <T> LinkedList<T> mergeLists(LinkedList<T> list1, LinkedList<T> list2) {
        list1.addAll(list2);
        return list1;
    }

    private class Range {

        final T min;
        final T max;

        private Range(T min, T max) {
            this.min = min;
            this.max = max;
        }

        boolean contains(T value) {
            var comparator = comparator();
            return comparator.compare(min, value) <= 0 && comparator.compare(value, max) <= 0;
        }

    }

}
