package essence.jdbc;

import essence.jdbc.mapping.ColumnAlias;
import essence.jdbc.mapping.Table;

import java.util.List;

public class PreparedSqlStatement<T> {

    private final Table<T> table;
    private final String sql;
    private final List<Parameter<?>> parameters;

    public PreparedSqlStatement(Table<T> table, String sql, List<Parameter<?>> parameters) {
        this.table = table;
        this.sql = sql;
        this.parameters = parameters;
    }

    public Table<T> getTable() {
        return table;
    }

    public String getSql() {
        return sql;
    }

    public List<Parameter<?>> getParameters() {
        return parameters;
    }

    public static class Parameter<V> {

        private final ColumnAlias<?, V> columnAlias;
        private final V value;

        public Parameter(ColumnAlias<?, V> columnAlias, V value) {
            this.columnAlias = columnAlias;
            this.value = value;
        }

        public ColumnAlias<?, V> getColumnAlias() {
            return columnAlias;
        }

        public V getValue() {
            return value;
        }

    }

}
