package essence.core.utils;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

public class ProximityUtils {

    public static class OrderedValues<T> {

        private final Function<Integer, T> lookup;
        private final Comparator<T> comparator;
        private final int size;

        private OrderedValues(Function<Integer, T> lookup, Comparator<T> comparator, int size) {
            this.lookup = lookup;
            this.comparator = comparator;
            this.size = size;
        }

        public T findClosestTo(T value) {
            return findClosestTo(value, 0, size);
        }

        private T findClosestTo(T value, int first, int last) {
            int size = last - first;
            if (size <= 0) {
                return null;
            } else if (size == 1) {
                return lookup.apply(first);
            } else {
                int i1 = first + (size - 1) / 2;
                int i2 = i1 + 1;
                T v1 = lookup.apply(i1);
                T v2 = lookup.apply(i2);
                int d1 = comparator.compare(value, v1);
                int d2 = comparator.compare(value, v2);
                if (d1 < 0) {
                    return findClosestTo(value, first, i2);
                } else if (d2 > 0) {
                    return findClosestTo(value, i2, last);
                } else {
                    return d1 < -d2 ? v1 : v2;
                }
            }
        }

    }

    public static <T> OrderedValues<T> inOrderedValues(Function<Integer, T> lookup, Comparator<T> comparator, int size) {
        return new OrderedValues<>(lookup, comparator, size);
    }

    public static <T extends Comparable<T>> OrderedValues<T> inOrderedValues(Function<Integer, T> lookup, int size) {
        return inOrderedValues(lookup, Comparator.naturalOrder(), size);
    }

    public static <T> OrderedValues<T> inOrderedValues(List<T> values, Comparator<T> comparator) {
        return inOrderedValues(values::get, comparator, values.size());
    }

    public static <T extends Comparable<T>> OrderedValues<T> inOrderedValues(List<T> values) {
        return inOrderedValues(values, Comparator.naturalOrder());
    }

    public static <T> OrderedValues<T> inOrderedValues(T[] values, Comparator<T> comparator) {
        return inOrderedValues(i -> values[i], comparator, values.length);
    }

    public static <T extends Comparable<T>> OrderedValues<T> inOrderedValues(T[] values) {
        return inOrderedValues(values, Comparator.naturalOrder());
    }

}
