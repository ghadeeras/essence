package essence.core.enumerables;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class Enumerables {

    private Enumerables() {
    }

    public static <T> OneOf<T> oneOf(Collection<T> values, Comparator<T> comparator) {
        return new OneOf<>(values, comparator);
    }

    public static <T> OneOf<T> oneOf(List<T> values) {
        return oneOf(values, Comparator.comparingInt(values::indexOf));
    }

    @SafeVarargs
    public static <T> OneOf<T> oneOf(Comparator<T> comparator, T... values) {
        return oneOf(Arrays.asList(values), comparator);
    }

    @SafeVarargs
    public static <T> OneOf<T> oneOf(T... values) {
        return oneOf(Arrays.asList(values));
    }

    @SafeVarargs
    public static <T extends Comparable<T>> OneOf<T> enumeration(T... values) {
        return oneOf(Arrays.asList(values), Comparator.naturalOrder());
    }

}
