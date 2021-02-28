package essence.core.enumerables;

import essence.core.basic.EnumerableType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import static essence.core.enumerables.Enumerables.oneOf;

public class OneOf<T> implements EnumerableType<T> {

    private final List<T> values;
    private final Comparator<T> comparator;
    private final Class<T> type;

    @SuppressWarnings("unchecked")
    OneOf(Collection<T> values, Comparator<T> comparator) {
        this.values = new ArrayList<>(values);
        this.comparator = comparator;
        this.values.sort(comparator);
        this.type = (Class<T>) values.stream()
            .map(OneOf::typeOf)
            .reduce(OneOf::commonAncestor)
            .orElse(Object.class);
    }

    private static <T> Class<?> typeOf(T value) {
        return value.getClass();
    }

    private static <T> Class<?> commonAncestor(Class<?> c1, Class<?> c2) {
        return c1.isAssignableFrom(c2) ?
            c1 :
            c2.isAssignableFrom(c1) ?
                c2 :
                commonAncestor(c2, c1.getSuperclass());
    }

    @Override
    public Class<T> javaType() {
        return type;
    }

    @Override
    public Comparator<T> comparator() {
        return comparator;
    }

    @Override
    public List<T> orderedValues() {
        return values;
    }

    public OneOf<T> orderedBy(Comparator<T> comparator) {
        return oneOf(values, comparator);
    }

}
