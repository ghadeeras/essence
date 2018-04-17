package essence.jdbc.mapping.columns;

import essence.jdbc.mapping.Column;
import essence.jdbc.mapping.Table;
import essence.jdbc.visitors.ColumnVisitor;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ColDecimal<T> extends Column<T, BigDecimal> {

    private final int precision;
    private final int scale;

    public ColDecimal(Table<T> table, boolean mandatory, Function<T, BigDecimal> getter, BiFunction<T, BigDecimal, T> setter, int precision, int scale) {
        super(table, mandatory, getter, setter);
        this.precision = precision;
        this.scale = scale;
    }

    @Override
    protected int sqlType() {
        return Types.DECIMAL;
    }

    @Override
    protected StatementSetter<BigDecimal> statementSetter() {
        return PreparedStatement::setBigDecimal;
    }

    @Override
    protected ResultByIndexGetter<BigDecimal> resultByIndexGetter() {
        return ResultSet::getBigDecimal;
    }

    @Override
    protected ResultByNameGetter<BigDecimal> resultByNameGetter() {
        return ResultSet::getBigDecimal;
    }

    @Override
    public <R> R accept(ColumnVisitor<R> visitor) {
        return visitor.visit(this);
    }

    public int getPrecision() {
        return precision;
    }

    public int getScale() {
        return scale;
    }

}
