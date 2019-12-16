package essence.core.ordinals;

import essence.core.basic.DataType;

import java.util.Comparator;
import java.util.function.BinaryOperator;

public abstract class OrdinalType<T> implements DataType<T> {

    protected abstract T min();

    protected abstract T max();

    protected abstract T next(T value);

    protected abstract T prev(T value);

    protected abstract Comparator<T> comparator();

    protected abstract T diff(T value1, T value2);

    protected boolean ordered(T value1, T value2) {
        return comparator().compare(value1, value2) < 0;
    }

    protected boolean isNegative(T value) {
        return ordered(value, identity());
    }

    protected T negate(T value) {
        return diff(identity(), value);
    }

    protected T absolute(T value) {
        return isNegative(value) ? negate(value) : value;
    }

    protected T distance(T value1, T value2) {
        return absolute(diff(value1, value2));
    }

    protected BinaryOperator<T> closerTo(T value) {
        return (v1, v2) -> {
            T d1 = distance(v1, value);
            T d2 = distance(v2, value);
            return ordered(d1, d2) ? v1 : v2;
        };
    }

    protected T add(T value1, T value2) {
        return diff(value1, negate(value2));
    }

}
