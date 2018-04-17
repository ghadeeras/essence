package essence.core.enumerables;

import essence.core.basic.EnumerableType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class OneOf<T> implements EnumerableType<T> {

    private final List<T> values;
    private final Comparator<T> comparator;

    public OneOf(Collection<T> values, Comparator<T> comparator) {
        this.values = new ArrayList<>(values);
        this.comparator = comparator;
        this.values.sort(comparator);
    }

    @Override
    public Comparator<T> comparator() {
        return comparator;
    }

    @Override
    public List<T> orderedValues() {
        return values;
    }

    @Override
    public T identity() {
        return values.stream().findFirst().orElse(null);
    }

}