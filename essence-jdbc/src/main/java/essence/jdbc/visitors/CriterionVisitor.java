package essence.jdbc.visitors;

import essence.jdbc.Criterion;

import static essence.jdbc.Criterion.*;

public interface CriterionVisitor<R> {

    interface ValueVisitor<R> {

        <V> R visit(LiteralValue<V> value, BinaryCriterion<V> criterion);

        <T, V> R visit(ReferencedValue<T, V> value, BinaryCriterion<V> criterion);

        default <V> R doVisit(Value<V> value, BinaryCriterion<V> criterion) {
            return value.accept(this, criterion);
        }

    }

    R visit(And and);

    R visit(Or or);

    <V> R visit(IsNull<V> isNull);

    <V> R visit(IsNotNull<V> isNotNull);

    <V> R visit(EqualTo<V> equalTo);

    <V> R visit(NotEqualTo<V> notEqualTo);

    default R doVisit(Criterion criterion) {
        return criterion.accept(this);
    }

}
