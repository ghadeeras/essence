package essence.jdbc.mapping;

public class TableAlias<T> {

    private final Table<T> table;
    private final String alias;

    public TableAlias(Table<T> table, String alias) {
        this.table = table;
        this.alias = alias;
    }

    public Table<T> getTable() {
        return table;
    }

    public String getAlias() {
        return alias;
    }

}
