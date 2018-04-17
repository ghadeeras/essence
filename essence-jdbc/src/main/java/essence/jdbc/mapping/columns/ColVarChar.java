package essence.jdbc.mapping.columns;

import essence.jdbc.mapping.Column;
import essence.jdbc.mapping.Table;
import essence.jdbc.visitors.ColumnVisitor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ColVarChar<T> extends Column<T, String> {

    private final int maxLength;

    public ColVarChar(Table<T> table, boolean mandatory, Function<T, String> getter, BiFunction<T, String, T> setter, int maxLength) {
        super(table, mandatory, getter, setter);
        this.maxLength = maxLength;
    }

    @Override
    protected int sqlType() {
        return Types.VARCHAR;
    }

    @Override
    protected StatementSetter<String> statementSetter() {
        return PreparedStatement::setString;
    }

    @Override
    protected ResultByIndexGetter<String> resultByIndexGetter() {
        return ResultSet::getString;
    }

    @Override
    protected ResultByNameGetter<String> resultByNameGetter() {
        return ResultSet::getString;
    }

    @Override
    public <R> R accept(ColumnVisitor<R> visitor) {
        return visitor.visit(this);
    }

    public int getMaxLength() {
        return maxLength;
    }

}
