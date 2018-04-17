package essence.jdbc;

import essence.jdbc.mapping.Column;
import essence.jdbc.mapping.ColumnAlias;
import essence.jdbc.mapping.Table;
import essence.jdbc.mapping.columns.*;
import essence.jdbc.visitors.ColumnVisitor;
import essence.jdbc.visitors.CriterionVisitor;

import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import static essence.jdbc.Criterion.*;
import static essence.jdbc.PreparedSqlStatement.Parameter;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class StandardSqlDialect implements SqlDialect {

    private static class CreateVisitor implements ColumnVisitor<String> {

        @Override
        public <T> String visit(ColBigInteger<T> column) {
            return "numeric(20)";
        }

        @Override
        public <T> String visit(ColDecimal<T> column) {
            return "numeric(" + column.getPrecision() + ", " + column.getScale() + ")";
        }

        @Override
        public <T> String visit(ColFlag<T> column) {
            return "boolean";
        }

        @Override
        public <T> String visit(ColInteger<T> column) {
            return "numeric(10)";
        }

        @Override
        public <T> String visit(ColVarChar<T> column) {
            return "varchar(" + column.getMaxLength() + ")";
        }

        private <T, V> String visit(Column<T, V> column) {
            return column.getName() + " " + column.accept(this) + (column.isMandatory() ? " not null" : "");
        }

    }

    private static class CriterionSqlVisitor implements CriterionVisitor<String> {

        static final ColumnAliasNaming withTableAlias =
            columnAlias -> columnAlias.getTableAlias().getAlias() + "." + columnAlias.getColumn().getName();

        static final ColumnAliasNaming withoutTableAlias =
            columnAlias -> columnAlias.getColumn().getName();

        private final ColumnAliasNaming columnAliasNaming;

        private CriterionSqlVisitor(ColumnAliasNaming columnAliasNaming) {
            this.columnAliasNaming = columnAliasNaming;
        }

        @Override
        public String visit(And and) {
            return toString(and, "and");
        }

        @Override
        public String visit(Or or) {
            return toString(or, "or");
        }


        @Override
        public <V> String visit(IsNull<V> isNull) {
            return toString(isNull, "is null");
        }

        @Override
        public <V> String visit(IsNotNull<V> isNotNull) {
            return toString(isNotNull, "is not null");
        }

        @Override
        public <V> String visit(EqualTo<V> equalTo) {
            return toString(equalTo, "=");
        }

        @Override
        public <V> String visit(NotEqualTo<V> notEqualTo) {
            return toString(notEqualTo, "<>");
        }

        private String toString(ColumnAlias<?, ?> columnAlias) {
            return columnAliasNaming.apply(columnAlias);
        }

        private <V> String toString(UnaryCriterion<V> criterion, String operator) {
            return toString(criterion.getColumnAlias()) + " " + operator;
        }

        private <V> String toString(BinaryCriterion<V> criterion, String operator) {
            return toString(criterion.getColumnAlias()) + sandwich(" ", " ").apply(operator) + criterion.getValue().accept(valueVisitor(), criterion);
        }

        private ValueVisitor<String> valueVisitor() {
            return new ValueVisitor<String>() {

                @Override
                public <V> String visit(LiteralValue<V> value, BinaryCriterion<V> criterion) {
                    return "?";
                }

                @Override
                public <T, V> String visit(ReferencedValue<T, V> value, BinaryCriterion<V> criterion) {
                    return CriterionSqlVisitor.this.toString(value.getColumnAlias());
                }

            };
        }

        private String toString(Criteria criteria, String operator) {
            String delimiter = sandwich(" ", " ").apply(operator);
            return criteria.getCriteria().stream()
                .map(this::doVisit)
                .map(sandwich("(", ")"))
                .collect(joining(delimiter));
        }

        private UnaryOperator<String> sandwich(String before, String after) {
            return s -> before + s + after;
        }

        interface ColumnAliasNaming extends Function<ColumnAlias<?, ?>, String> {}

    }

    private static class CriterionParametersVisitor implements CriterionVisitor<Stream<Parameter<?>>> {

        @Override
        public Stream<Parameter<?>> visit(And and) {
            return parameters(and);
        }

        @Override
        public Stream<Parameter<?>> visit(Or or) {
            return parameters(or);
        }

        @Override
        public <V> Stream<Parameter<?>> visit(IsNull<V> isNull) {
            return Stream.empty();
        }

        @Override
        public <V> Stream<Parameter<?>> visit(IsNotNull<V> isNotNull) {
            return Stream.empty();
        }

        @Override
        public <V> Stream<Parameter<?>> visit(EqualTo<V> equalTo) {
            return parameters(equalTo);
        }

        @Override
        public <V> Stream<Parameter<?>> visit(NotEqualTo<V> notEqualTo) {
            return parameters(notEqualTo);
        }

        private <V> Stream<Parameter<?>> parameters(BinaryCriterion<V> binaryCriterion) {
            return binaryCriterion.getValue().accept(valueVisitor(), binaryCriterion);
        }

        private Stream<Parameter<?>> parameters(Criteria criteria) {
            return criteria.getCriteria().stream().flatMap(this::doVisit);
        }

        private ValueVisitor<Stream<Parameter<?>>> valueVisitor() {
            return new ValueVisitor<Stream<Parameter<?>>>() {

                @Override
                public <V> Stream<Parameter<?>> visit(LiteralValue<V> value, BinaryCriterion<V> criterion) {
                    return Stream.of(new Parameter<>(criterion.getColumnAlias(), value.getValue()));
                }

                @Override
                public <T, V> Stream<Parameter<?>> visit(ReferencedValue<T, V> value, BinaryCriterion<V> criterion) {
                    return Stream.empty();
                }

            };
        }

    }

    @Override
    public <T> String create(Table<T> table) {
        CreateVisitor visitor = new CreateVisitor();
        return "create table " + table.getName() + "(\n  " + table.getColumns().stream()
            .map(visitor::visit)
            .collect(joining(",\n  "))
        + "\n)";
    }

    @Override
    public <T> PreparedSqlStatement<T> insertInto(Table<T> table, T entity) {
        return new PreparedSqlStatement<>(
            table,
            "insert into " + table.getName() + " (" + table.getColumns().stream()
                .map(Column::getName)
                .collect(joining(", "))
            + ") values (" + table.getColumns().stream()
                .map(column -> "?")
                .collect(joining(", "))
            + ")",
            table.getColumns().stream()
                .map(Column::aliased)
                .map(parameter(entity))
                .collect(toList())
        );
    }

    @Override
    public <T> PreparedSqlStatement<T> deleteFrom(Table<T> table, Criterion criterion) {
        PreparedSqlStatement<T> whereClause = where(table, criterion, CriterionSqlVisitor.withoutTableAlias);
        return new PreparedSqlStatement<>(
            table,
            "delete from " + table.getName() + " where " + whereClause.getSql(),
            whereClause.getParameters()
        );
    }

    @Override
    public <T> PreparedSqlStatement<T> selectFrom(Table<T> table, Criterion criterion) {
        PreparedSqlStatement<T> whereClause = where(table, criterion, CriterionSqlVisitor.withoutTableAlias);
        return new PreparedSqlStatement<>(
            table, "select " + table.getColumns().stream()
                .map(Column::getName)
                .collect(joining(", ")) +
            " from " + table.getName() +
            " where " + whereClause.getSql(),
            whereClause.getParameters()
        );
    }

    private <T> PreparedSqlStatement<T> where(Table<T> table, Criterion criterion, CriterionSqlVisitor.ColumnAliasNaming naming) {
        return new PreparedSqlStatement<>(
            table,
            criterion.accept(new CriterionSqlVisitor(naming)),
            criterion.accept(new CriterionParametersVisitor()).collect(toList())
        );
    }

    private <T, V> Function<ColumnAlias<T, V>, Parameter<V>> parameter(T entity) {
        return columnAlias -> new Parameter<>(columnAlias, columnAlias.getColumn().get(entity));
    }

}
