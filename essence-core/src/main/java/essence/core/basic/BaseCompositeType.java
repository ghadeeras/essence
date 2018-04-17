package essence.core.basic;

import essence.core.random.RandomGenerator;
import essence.core.utils.LazyValue;
import essence.core.validation.ValidationReporter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import static essence.core.utils.StreamUtils.reduce;

public class BaseCompositeType<T> implements DataType<T> {

    protected final Supplier<T> constructor;
    protected final List<Member<T, ?, ?>> definedMembers = new ArrayList<>();

    private final LazyValue<List<Member<T, ?, ?>>> members = LazyValue.from(
            () -> Collections.unmodifiableList(definedMembers)
    );

    public BaseCompositeType(Supplier<T> constructor) {
        this.constructor = constructor;
    }

    protected <C, D, M extends Member<T, C, D>> M define(M child) {
        definedMembers.add(child);
        return child;
    }

    public List<Member<T, ?, ?>> getMembers() {
        return members.get();
    }

    public T construct() {
        return constructor.get();
    }

    @Override
    public T identity() {
        return construct();
    }

    @Override
    public T randomValue(RandomGenerator generator) {
        return reduce(getMembers().stream(), identity(), (p, m) -> m.randomize(p, generator));
    }

    @Override
    public T closestTo(T value, ValidationReporter reporter) {
        return reduce(getMembers().stream(), value, (p, m) -> m.closestTo(p, reporter));
    }

    @SafeVarargs
    public final ConstrainedType<T> with(ConstrainedType.MemberDefiner<T>... definers) {
        return new ConstrainedType<>(this, definers);
    }

}
