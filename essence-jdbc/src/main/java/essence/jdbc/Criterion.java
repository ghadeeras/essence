package essence.jdbc;

import essence.jdbc.mapping.ColumnAlias;
import essence.jdbc.visitors.CriterionVisitor;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public abstract class Criterion {

    public abstract Criterion negated();

    public abstract <R> R accept(CriterionVisitor<R> visitor);

    public And and(Criterion that) {
        return new And(this, that);
    }

    public Or or(Criterion that) {
        return new Or(this, that);
    }

    public <T, V, C extends AtomicCriterion<V>> And and(ColumnAlias<T, V> lhs, RHS<T, V, C> rhs) {
        return and(where(lhs, rhs));
    }

    public <T, V, C extends AtomicCriterion<V>> Or or(ColumnAlias<T, V> lhs, RHS<T, V, C> rhs) {
        return or(where(lhs, rhs));
    }

    public static abstract class Value<V> {

        public abstract <R> R accept(CriterionVisitor.ValueVisitor<R> visitor, BinaryCriterion<V> criterion);

    }

    public static final class LiteralValue<V> extends Value<V> {

        private final V value;

        public LiteralValue(V value) {
            this.value = value;
        }

        public V getValue() {
            return value;
        }

        @Override
        public <R> R accept(CriterionVisitor.ValueVisitor<R> visitor, BinaryCriterion<V> criterion) {
            return visitor.visit(this, criterion);
        }
    }

    public static final class ReferencedValue<T, V> extends Value<V> {

        private final ColumnAlias<T, V> columnAlias;

        public ReferencedValue(ColumnAlias<T, V> columnAlias) {
            this.columnAlias = columnAlias;
        }

        public ColumnAlias<T, V> getColumnAlias() {
            return columnAlias;
        }

        @Override
        public <R> R accept(CriterionVisitor.ValueVisitor<R> visitor, BinaryCriterion<V> criterion) {
            return visitor.visit(this, criterion);
        }

    }

    public static abstract class Criteria extends Criterion {

        private final Criterion c1;
        private final Criterion c2;
        private final Criterion[] rest;

        private final Collection<Criterion> criteria;

        protected Criteria(Criterion c1, Criterion c2, Criterion... rest) {
            this.c1 = c1;
            this.c2 = c2;
            this.rest = rest;

            this.criteria = Stream.concat(
                Stream.of(c1, c2).flatMap(this::streamed),
                Stream.of(rest).flatMap(this::streamed)
            ).collect(toList());
        }

        private Stream<Criterion> streamed(Criterion c) {
            return getClass().isInstance(c) ? getClass().cast(c).getCriteria().stream() : Stream.of(c);
        }

        public Collection<Criterion> getCriteria() {
            return criteria;
        }

        @Override
        public Criterion negated() {
            return negated(c1.negated(), c2.negated(), Stream.of(rest).map(Criterion::negated).toArray(Criterion[]::new));
        }

        protected abstract Criterion negated(Criterion c1, Criterion c2, Criterion[] rest);

    }

    public static final class And extends Criteria {

        public And(Criterion c1, Criterion c2, Criterion... criteria) {
            super(c1, c2, criteria);
        }

        @Override
        protected Criterion negated(Criterion c1, Criterion c2, Criterion[] rest) {
            return new Or(c1, c2, rest);
        }

        @Override
        public <R> R accept(CriterionVisitor<R> visitor) {
            return visitor.visit(this);
        }

    }

    public static final class Or extends Criteria {

        public Or(Criterion c1, Criterion c2, Criterion... criteria) {
            super(c1, c2, criteria);
        }

        @Override
        protected Criterion negated(Criterion c1, Criterion c2, Criterion[] rest) {
            return new And(c1, c2, rest);
        }

        @Override
        public <R> R accept(CriterionVisitor<R> visitor) {
            return visitor.visit(this);
        }

    }

    public static abstract class AtomicCriterion<V> extends Criterion {

        private final ColumnAlias<?, V> columnAlias;

        protected AtomicCriterion(ColumnAlias<?, V> columnAlias) {
            this.columnAlias = columnAlias;
        }

        public ColumnAlias<?, V> getColumnAlias() {
            return columnAlias;
        }

        @Override
        public abstract AtomicCriterion<V> negated();

    }

    public static abstract class UnaryCriterion<V> extends AtomicCriterion<V> {

        protected UnaryCriterion(ColumnAlias<?, V> columnAlias) {
            super(columnAlias);
        }

    }

    public static abstract class BinaryCriterion<V> extends AtomicCriterion<V> {

        private final Value<V> value;

        protected BinaryCriterion(ColumnAlias<?, V> columnAlias, Value<V> value) {
            super(columnAlias);
            this.value = value;
        }

        public Value<V> getValue() {
            return value;
        }

    }

    public static final class IsNull<V> extends UnaryCriterion<V> {

        public IsNull(ColumnAlias<?, V> columnAlias) {
            super(columnAlias);
        }

        @Override
        public AtomicCriterion<V> negated() {
            return new IsNotNull<>(getColumnAlias());
        }

        @Override
        public <R> R accept(CriterionVisitor<R> visitor) {
            return visitor.visit(this);
        }

    }

    public static final class IsNotNull<V> extends UnaryCriterion<V> {

        public IsNotNull(ColumnAlias<?, V> columnAlias) {
            super(columnAlias);
        }

        @Override
        public AtomicCriterion<V> negated() {
            return new IsNull<>(getColumnAlias());
        }

        @Override
        public <R> R accept(CriterionVisitor<R> visitor) {
            return visitor.visit(this);
        }

    }

    public static final class EqualTo<V> extends BinaryCriterion<V> {

        protected EqualTo(ColumnAlias<?, V> columnAlias, Value<V> value) {
            super(columnAlias, value);
        }

        @Override
        public AtomicCriterion<V> negated() {
            return new NotEqualTo<>(getColumnAlias(), getValue());
        }

        @Override
        public <R> R accept(CriterionVisitor<R> visitor) {
            return visitor.visit(this);
        }

    }

    public static final class NotEqualTo<V> extends BinaryCriterion<V> {

        protected NotEqualTo(ColumnAlias<?, V> columnAlias, Value<V> value) {
            super(columnAlias, value);
        }

        @Override
        public AtomicCriterion<V> negated() {
            return new EqualTo<>(getColumnAlias(), getValue());
        }

        @Override
        public <R> R accept(CriterionVisitor<R> visitor) {
            return visitor.visit(this);
        }

    }

    public interface RHS<T, V, C extends AtomicCriterion<V>> extends Function<ColumnAlias<T, V>, C> {

        default RHS<T, V, AtomicCriterion<V>> negated() {
            return alias -> where(alias, this).negated();
        }

        static <T, V> RHS<T, V, IsNull<V>> isNull() {
            return IsNull::new;
        }

        static <T, V> RHS<T, V, IsNotNull<V>> isNotNull() {
            return IsNotNull::new;
        }

        static <T, V> RHS<T, V, EqualTo<V>> equalTo(V rhs) {
            return alias -> new EqualTo<>(alias, new LiteralValue<>(rhs));
        }

        static <T, V> RHS<T, V, EqualTo<V>> equalTo(ColumnAlias<T, V> rhs) {
            return alias -> new EqualTo<>(alias, new ReferencedValue<>(rhs));
        }

        static <T, V> RHS<T, V, NotEqualTo<V>> notEqualTo(V rhs) {
            return alias -> new NotEqualTo<>(alias, new LiteralValue<>(rhs));
        }

        static <T, V> RHS<T, V, NotEqualTo<V>> notEqualTo(ColumnAlias<T, V> rhs) {
            return alias -> new NotEqualTo<>(alias, new ReferencedValue<>(rhs));
        }

    }

    public static <T, V, C extends AtomicCriterion<V>> C where(ColumnAlias<T, V> lhs, RHS<T, V, C> rhs) {
        return rhs.apply(lhs);
    }

}
