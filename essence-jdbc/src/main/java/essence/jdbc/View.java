package essence.jdbc;

import essence.jdbc.mapping.Table;

import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public abstract class View<T> {

    public <R> On<T, R> leftJoin(View<R> right) {
        return join(right, false);
    }

    public <R> On<T, R> innerJoin(View<R> right) {
        return join(right, true);
    }

    private <R> On<T, R> join(View<R> right, boolean inner) {
        return new On<>((joinPairs, joiner) -> new CompositeView<>(this, right, new Join<>(inner, joinPairs, joiner)));
    }

    public class On<L, R> {

        private final BiFunction<List<Join.Pair<L, R, ?>>, BiFunction<L, R, L>, CompositeView<L, R>> impl;

        private On(BiFunction<List<Join.Pair<L, R, ?>>, BiFunction<L, R, L>, CompositeView<L, R>> impl) {
            this.impl = impl;
        }

        JoiningBy<L, R> on(List<Join.Pair<L, R, ?>> joinPairs) {
            return joiner -> impl.apply(joinPairs, joiner);
        }

        @SafeVarargs
        public final JoiningBy<L, R> on(Join.Pair<L, R, ?>... joinPairs) {
            assert joinPairs != null && joinPairs.length > 0;
            return on(Stream.of(joinPairs).collect(toList()));
        }

    }

    public interface JoiningBy<L, R> {

        CompositeView<L, R> joiningBy(BiFunction<L, R, L> joiner);

    }

    public static class TableAlias<T> extends View<T> {

        private final Table<T> table;
        private final String alias;

        public TableAlias(Table<T> table, String alias) {
            this.table = table;
            this.alias = alias;
        }

        public Table<T> getTable() {
            return table;
        }

        public String getAlias() {
            return alias;
        }

    }

    public static class CompositeView<L, R> extends View<L> {

        private final View<L> left;
        private final View<R> right;
        private final Join<L, R> join;

        public CompositeView(View<L> left, View<R> right, Join<L, R> join) {
            this.left = left;
            this.right = right;
            this.join = join;
        }

        public View<L> getLeft() {
            return left;
        }

        public View<R> getRight() {
            return right;
        }

        public Join<L, R> getJoin() {
            return join;
        }

    }

}
