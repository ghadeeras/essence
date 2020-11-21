package essence.core.basic;

import essence.core.utils.BasicCollector;
import essence.core.utils.Reference;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Stream;

public class UniqueMember<TParent, T> extends Member<TParent, T, T> {

    private final Collector<T, Reference<T>, T> collector = BasicCollector.collector(
        Reference::new,
        Reference::set,
        Reference::get
    );

    public UniqueMember(
        Function<Member, String> name,
        DataType<T> type,
        BaseCompositeType<TParent> parentType,
        boolean mandatory,
        Function<TParent, T> getter,
        BiFunction<TParent, T, TParent> setter
    ) {
        super(name, type, parentType, mandatory ? 1 : 0, 1, getter, setter);
    }

    @Override
    public <R> R accept(MemberVisitor<TParent, T, R> visitor) {
        return visitor.visit(this);
    }

    @Override
    public Collector<T, ?, T> collector() {
        return collector;
    }

    @Override
    public Stream<T> stream(T v) {
        return Stream.of(v).filter(Objects::nonNull);
    }

    @Override
    public int size(T v) {
        return v != null ? 1 : 0;
    }

}
