package essence.jdbc.mapping.columns;

import essence.jdbc.mapping.Column;
import essence.jdbc.mapping.Table;
import essence.jdbc.visitors.ColumnVisitor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ColBigInteger<T> extends Column<T, Long> {

    public ColBigInteger(Table<T> table, boolean mandatory, Function<T, Long> getter, BiFunction<T, Long, T> setter) {
        super(table, mandatory, getter, setter);
    }

    @Override
    protected int sqlType() {
        return Types.BIGINT;
    }

    @Override
    protected StatementSetter<Long> statementSetter() {
        return PreparedStatement::setLong;
    }

    @Override
    protected ResultByIndexGetter<Long> resultByIndexGetter() {
        return ResultSet::getLong;
    }

    @Override
    protected ResultByNameGetter<Long> resultByNameGetter() {
        return ResultSet::getLong;
    }

    @Override
    public <R> R accept(ColumnVisitor<R> visitor) {
        return visitor.visit(this);
    }

}
