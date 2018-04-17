package essence.jdbc;

import essence.jdbc.mapping.Table;

public interface SqlDialect {

    <T> String create(Table<T> table);

    <T> PreparedSqlStatement<T> insertInto(Table<T> table, T entity);

    <T> PreparedSqlStatement<T> deleteFrom(Table<T> table, Criterion criterion);

    <T> PreparedSqlStatement<T> selectFrom(Table<T> table, Criterion criterion);

}
