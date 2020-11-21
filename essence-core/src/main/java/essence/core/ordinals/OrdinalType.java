package essence.core.ordinals;

import essence.core.basic.DataType;
import essence.core.random.RandomGenerator;
import essence.core.utils.LazyValue;
import essence.core.validation.ValidationReporter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import static java.util.Collections.singletonList;

public abstract class OrdinalType<T, D extends Comparable<D>> implements DataType<T> {

    private final Subset<T> subset;
    private final LazyValue<List<Range>> ranges = LazyValue.from(this::inclusiveRanges);

    protected OrdinalType(Subset<T> subset) {
        this.subset = subset;
    }

    private List<Range> ranges() {
        return ranges.get();
    }

    protected Subset<T> subSet(SubsetConstructor<T> subsetConstructor) {
        SubsetOperations<T> setOps = new SubsetOperations<>(this);
        Subset<T> subset = subsetConstructor.apply(setOps);
        return setOps.intersection(subset, this.subset);
    }

    @Override
    public T randomValue(RandomGenerator generator) {
        int rangeIndex = generator.nextInt(0, ranges().size());
        Range range = ranges().get(rangeIndex);
        return add(range.min, randomDistance(generator, distance(range.min, range.max)));
    }

    @Override
    public void validate(T value, ValidationReporter reporter) {
        if (ranges().stream().noneMatch(range -> range.contains(value))) {
            reporter.report(this, value, () -> value + " is not in " + subset);
        }
    }

    @SafeVarargs
    protected final boolean ordered(T... values) {
        boolean ordered = true;
        for (int i = 1; ordered && i < values.length; i++) {
            ordered = comparator().compare(values[i - 1], values[i]) < 0;
        }
        return ordered;
    }

    protected Comparator<T> comparator() {
        return Comparator.comparing(o -> distance(min(), o));
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
            singletonList(new Range(min(), max()));
    }

    private Limit<T> toInclusive(Limit<T> limit) {
        T value = limit.getValue();
        return limit.isExclusive() ?
            limit.isLower() ?
                new Limit<>(next(value), true, true) :
                new Limit<>(prev(value), false, true) :
            limit;
    }

    private LinkedList<Range> addToRanges(LinkedList<Range> ranges, Limit<T> limit) {
        ranges.add(limit.isLower() ?
            new Range(limit.getValue(), max()) :
            ranges.isEmpty() ?
                new Range(min(), limit.getValue()) :
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
            return ordered(min, value, max);
        }

    }

    public interface SubsetConstructor<T> extends Function<SubsetOperations<T>, Subset<T>> {}

}
