package essence.jdbc.mapping.columns;

import essence.jdbc.mapping.Column;
import essence.jdbc.mapping.Table;
import essence.jdbc.visitors.ColumnVisitor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ColInteger<T> extends Column<T, Integer> {

    public ColInteger(Table<T> table, boolean mandatory, Function<T, Integer> getter, BiFunction<T, Integer, T> setter) {
        super(table, mandatory, getter, setter);
    }

    @Override
    protected int sqlType() {
        return Types.INTEGER;
    }

    @Override
    protected StatementSetter<Integer> statementSetter() {
        return PreparedStatement::setInt;
    }

    @Override
    protected ResultByIndexGetter<Integer> resultByIndexGetter() {
        return ResultSet::getInt;
    }

    @Override
    protected ResultByNameGetter<Integer> resultByNameGetter() {
        return ResultSet::getInt;
    }

    @Override
    public <R> R accept(ColumnVisitor<R> visitor) {
        return visitor.visit(this);
    }

}
