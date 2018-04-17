package essence.jdbc.mapping;

public class ColumnAlias<T, V> {

    private final TableAlias<T> tableAlias;
    private final Column<T, V> column;

    public ColumnAlias(TableAlias<T> tableAlias, Column<T, V> column) {
        this.tableAlias = tableAlias;
        this.column = column;
    }

    public TableAlias<T> getTableAlias() {
        return tableAlias;
    }

    public Column<T, V> getColumn() {
        return column;
    }

}
