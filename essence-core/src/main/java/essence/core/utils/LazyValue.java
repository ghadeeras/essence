package essence.core.utils;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;

public class LazyValue<T> implements Supplier<T> {

    private final Supplier<T> supplier;

    private AtomicReference<T> value = new AtomicReference<>(null);

    public LazyValue(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public static <T> LazyValue<T> from(Supplier<T> supplier) {
        return new LazyValue<>(supplier);
    }

    public <M> LazyValue<M> map(Function<T, M> mapper) {
        return LazyValue.from(() -> mapper.apply(supplier.get()));
    }

    public <M> LazyValue<M> flatMap(Function<T, LazyValue<M>> mapper) {
        return LazyValue.from(() -> mapper.apply(supplier.get()).get());
    }

    @Override
    public T get() {
        T v = value.get();
        return v != null ? v : doGet();
    }

    private synchronized T doGet() {
        T v = value.get();
        if (v == null) {
            v = supplier.get();
            value.set(v);
        }
        return v;
    }

}
