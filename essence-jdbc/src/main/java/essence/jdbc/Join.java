package essence.jdbc;

import java.util.List;
import java.util.function.BiFunction;

public class Join<L, R> {

    private final boolean inner;
    private final List<Pair<L, R, ?>> pairs;
    private final BiFunction<L, R, L> joiner;

    public Join(boolean inner, List<Pair<L, R, ?>> pairs, BiFunction<L, R, L> joiner) {
        this.inner = inner;
        this.pairs = pairs;
        this.joiner = joiner;
    }

    public List<Pair<L, R, ?>> getPairs() {
        return pairs;
    }

    public boolean isInner() {
        return inner;
    }

    public static class Pair<L, R, V> {

        private final ColumnAlias<L, V> left;
        private final ColumnAlias<R, V> right;

        public Pair(ColumnAlias<L, V> left, ColumnAlias<R, V> right) {
            this.left = left;
            this.right = right;
        }

        public ColumnAlias<L, V> getLeft() {
            return left;
        }

        public ColumnAlias<R, V> getRight() {
            return right;
        }

    }

}
