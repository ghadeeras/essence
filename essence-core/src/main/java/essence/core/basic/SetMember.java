package essence.core.basic;

import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SetMember<TParent, T> extends Member<TParent, Set<T>, T> {

    public SetMember(
        Function<Member, String> name,
        DataType<T> type,
        BaseCompositeType<TParent> parentType,
        int minMultiplicity,
        int maxMultiplicity,
        Function<TParent, Set<T>> getter,
        BiFunction<TParent, Set<T>, TParent> setter
    ) {
        super(name, type, parentType, minMultiplicity, maxMultiplicity, getter, setter);
    }

    @Override
    public <R> R accept(MemberVisitor<TParent, T, R> visitor) {
        return visitor.visit(this);
    }

    @Override
    public Collector<T, ?, Set<T>> collector() {
        return Collectors.toSet();
    }

    @Override
    public Stream<T> stream(Set<T> v) {
        return v.stream();
    }

    @Override
    public int size(Set<T> v) {
        return v.size();
    }

}
