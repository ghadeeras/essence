package essence.core.basic;

import essence.core.random.RandomGenerator;
import essence.core.utils.LazyValue;
import essence.core.utils.Pair;
import essence.core.validation.ValidationReporter;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Stream;

import static essence.core.random.RandomGeneration.generator;
import static java.lang.Integer.max;
import static java.lang.Integer.min;
import static java.util.stream.Collectors.*;
import static java.util.stream.IntStream.range;

public abstract class Member<TParent, TContainer, T> {

    private final LazyValue<String> name;
    private final DataType<T> type;
    private final BaseCompositeType<TParent> parentType;
    private final int minMultiplicity;
    private final int maxMultiplicity;
    private final int maxRandomMultiplicity;
    private final Function<TParent, TContainer> getter;
    private final BiFunction<TParent, TContainer, TParent> setter;

    public Member(
        Function<Member, String> name,
        DataType<T> type,
        BaseCompositeType<TParent> parentType,
        int minMultiplicity,
        int maxMultiplicity,
        Function<TParent, TContainer> getter,
        BiFunction<TParent, TContainer, TParent> setter
    ) {
        this.name = LazyValue.from(() -> name.apply(this));
        this.type = type;
        this.parentType = parentType;
        this.minMultiplicity = minMultiplicity;
        this.maxMultiplicity = maxMultiplicity;
        this.maxRandomMultiplicity = min(maxMultiplicity, minMultiplicity + 8) + 1;
        this.getter = getter;
        this.setter = setter;
    }

    public Map<T, List<TParent>> index(Collection<TParent> parents) {
        return parents.stream()
            .flatMap(v -> valuesFrom(v).map(k -> new Pair<>(k, v)))
            .collect(groupingBy(Pair::getKey, mapping(Pair::getValue, toList())));
    }

    public TContainer of(TParent parent) {
        return getter.apply(parent);
    }

    public Stream<T> valuesFrom(TParent parent) {
        return stream(of(parent));
    }

    public TParent update(TParent parent, TContainer value) {
        return setter.apply(parent, value);
    }

    public TParent update(TParent parent, Stream<T> values) {
        return setter.apply(parent, values.collect(collector()));
    }

    public abstract <R> R accept(MemberVisitor<TParent, T, R> visitor);

    public abstract Collector<T, ?, TContainer> collector();

    public abstract Stream<T> stream(TContainer v);

    public abstract int size(TContainer v);

    public TParent randomize(TParent parent) {
        return randomize(parent, generator());
    }

    public TParent randomize(TParent parent, RandomGenerator generator) {
        int size = generator.nextInt(minMultiplicity, maxRandomMultiplicity);
        TContainer value = Stream.generate(() -> type.randomValue(generator)).limit(size).collect(collector());
        return update(parent, value);
    }

    public TParent closestTo(TParent parent, ValidationReporter reporter) {
        TContainer oldValue = of(parent);

        int size = size(oldValue);
        Stream<T> limitedToMaxMultiplicity =
                stream(oldValue).limit(min(size, maxMultiplicity)).map(v -> type.closestTo(v, reporter));
        Stream<T> neededToMinMultiplicity =
                range(0, max(size, minMultiplicity) - size).mapToObj(i -> type.closestToIdentity());

        TContainer newValue = Stream.concat(limitedToMaxMultiplicity, neededToMinMultiplicity).collect(collector());
        return update(parent, newValue);
    }

    public String getName() {
        return name.get();
    }

    public DataType<T> getType() {
        return type;
    }

    public BaseCompositeType<TParent> getParentType() {
        return parentType;
    }

    public Integer getMinMultiplicity() {
        return minMultiplicity;
    }

    public Integer getMaxMultiplicity() {
        return maxMultiplicity;
    }

    public Function<TParent, TContainer> getGetter() {
        return getter;
    }

    public BiFunction<TParent, TContainer, TParent> getSetter() {
        return setter;
    }

}
