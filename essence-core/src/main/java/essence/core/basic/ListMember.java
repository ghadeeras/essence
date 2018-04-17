package essence.core.basic;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ListMember<TParent, T> extends Member<TParent, List<T>, T> {

    public ListMember(
        Function<Member, String> name,
        DataType<T> type,
        BaseCompositeType<TParent> parentType,
        int minMultiplicity,
        int maxMultiplicity,
        Function<TParent, List<T>> getter,
        BiFunction<TParent, List<T>, TParent> setter
    ) {
        super(name, type, parentType, minMultiplicity, maxMultiplicity, getter, setter);
    }

    @Override
    public <R> R accept(MemberVisitor<TParent, T, R> visitor) {
        return visitor.visit(this);
    }

    @Override
    public Collector<T, ?, List<T>> collector() {
        return Collectors.toList();
    }

    @Override
    public Stream<T> stream(List<T> v) {
        return v.stream();
    }

    @Override
    public int size(List<T> v) {
        return v.size();
    }

}
