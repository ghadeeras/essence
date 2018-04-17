package essence.jdbc.mapping;

import essence.core.utils.LazyValue;
import essence.jdbc.visitors.ColumnVisitor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.util.function.Function.identity;

public abstract class Column<T, V> {

    private final Table<T> table;
    private final LazyValue<String> name;

    private final boolean mandatory;
    private final Function<T, V> getter;
    private final BiFunction<T, V, T> setter;

    public Column(Table<T> table, boolean mandatory, Function<T, V> getter, BiFunction<T, V, T> setter) {
        this.table = table;
        this.name = LazyValue.from(() -> table.columnNaming.apply(this));
        this.mandatory = mandatory;
        this.getter = getter;
        this.setter = setter;
    }

    public void copy(PreparedStatement statement, int index, T object) throws SQLException {
        set(statement, index, getter.apply(object));
    }

    public T copy(ResultSet resultSet, int index, T object) throws SQLException {
        return  setter.apply(object, get(resultSet, index));
    }

    public T copy(ResultSet resultSet, String name, T object) throws SQLException {
        return  setter.apply(object, get(resultSet, name));
    }

    public T copy(ResultSet resultSet, Function<String, String> aliasing, T object) throws SQLException {
        return  setter.apply(object, get(resultSet, aliasing));
    }

    public T copy(ResultSet resultSet, T object) throws SQLException {
        return  setter.apply(object, get(resultSet));
    }

    public V get(T entity) {
        return getter.apply(entity);
    }

    public V get(ResultSet resultSet) throws SQLException {
        return get(resultSet, identity());
    }

    public V get(ResultSet resultSet, Function<String, String> aliasing) throws SQLException {
        return get(resultSet, aliasing.apply(getName()));
    }

    public V get(ResultSet resultSet, int index) throws SQLException {
        V value = resultByIndexGetter().get(resultSet, index);
        return !resultSet.wasNull() ? value : null;
    }

    public V get(ResultSet resultSet, String name) throws SQLException {
        V value = resultByNameGetter().get(resultSet, name);
        return !resultSet.wasNull() ? value : null;
    }

    public T set(T entity, V value) throws SQLException {
        return setter.apply(entity, value);
    }

    public void set(PreparedStatement statement, int index, V value) throws SQLException {
        if (value != null) {
            statementSetter().set(statement, index, value);
        } else {
            statement.setNull(index, sqlType());
        }
    }

    public Table<T> getTable() {
        return table;
    }

    public String getName() {
        return name.get();
    }

    public boolean isMandatory() {
        return mandatory;
    }

    protected abstract int sqlType();
    protected abstract StatementSetter<V> statementSetter();
    protected abstract ResultByIndexGetter<V> resultByIndexGetter();
    protected abstract ResultByNameGetter<V> resultByNameGetter();

    public abstract <R> R accept(ColumnVisitor<R> visitor);

    public ColumnAlias<T, V> of(TableAlias<T> tableAlias) {
        return new ColumnAlias<>(tableAlias, this);
    }

    public ColumnAlias<T, V> aliased() {
        return of(table.defaultAlias);
    }

    protected interface StatementSetter<V> {

        void set(PreparedStatement statement, int index, V value) throws SQLException;

    }

    protected interface ResultByIndexGetter<V> {

        V get(ResultSet resultSet, int index) throws SQLException;

    }

    protected interface ResultByNameGetter<V> {

        V get(ResultSet resultSet, String name) throws SQLException;

    }

}
