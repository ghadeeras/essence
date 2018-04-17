package essence.jdbc.mapping.columns;

import essence.jdbc.mapping.Column;
import essence.jdbc.mapping.Table;
import essence.jdbc.visitors.ColumnVisitor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ColFlag<T> extends Column<T, Boolean> {

    public ColFlag(Table<T> table, boolean mandatory, Function<T, Boolean> getter, BiFunction<T, Boolean, T> setter) {
        super(table, mandatory, getter, setter);
    }

    @Override
    protected int sqlType() {
        return Types.BOOLEAN;
    }

    @Override
    protected StatementSetter<Boolean> statementSetter() {
        return PreparedStatement::setBoolean;
    }

    @Override
    protected ResultByIndexGetter<Boolean> resultByIndexGetter() {
        return ResultSet::getBoolean;
    }

    @Override
    protected ResultByNameGetter<Boolean> resultByNameGetter() {
        return ResultSet::getBoolean;
    }

    @Override
    public <R> R accept(ColumnVisitor<R> visitor) {
        return visitor.visit(this);
    }

}
