package essence.core.utils;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Stream;

public interface Equality<T> {

    boolean check(T thisValue, T thatValue);

    default boolean areEqual(T thisValue, T thatValue) {
        return thisValue == thatValue || thisValue != null && thatValue != null && check(thisValue, thatValue);
    }

    static <T, A> Equality<T> equalFor(Function<T, ? extends A> getter) {
        return equalFor(getter, Objects::equals);
    }

    static <T, A> Equality<T> equalFor(Function<T, ? extends A> getter, Equality<? super A> equality) {
        return (thisValue, thatValue) -> equality.areEqual(getter.apply(thatValue), getter.apply(thatValue));
    }

    @SafeVarargs
    static <T> Equality<T> when(Equality<? super T>... equalities) {
        return (thisValue, thatValue) -> Stream.of(equalities).allMatch(e -> e.check(thisValue, thatValue));
    }

    static <T> BiPredicate<T, Object> predicate(Class<? extends T> type, Equality<? super T> equality) {
        return (thisValue, thatValue) -> thisValue == thatValue || type.isInstance(thatValue) && equality.check(thisValue, type.cast(thatValue));
    }

    static <T, C extends Collection<T>> Equality<C> inSameOrder() {
        return inSameOrder(Objects::equals);
    }

    static <T, C extends Collection<T>> Equality<C> inSameOrder(Equality<T> equality) {
        return (thisValue, thatValue) -> {
            boolean result = thisValue.size() == thatValue.size();
            Iterator<T> thisIterator = thisValue.iterator();
            Iterator<T> thatIterator = thatValue.iterator();
            while (result && thisIterator.hasNext() && thatIterator.hasNext()) {
                result = equality.areEqual(thisIterator.next(), thatIterator.next());
            }
            result = result && (thisIterator.hasNext() == thatIterator.hasNext());
            return result;
        };
    }

}
