package essence.jdbc;

import essence.jdbc.mapping.Column;
import essence.jdbc.mapping.Table;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class SqlExecutor {

    private final DataSource dataSource;

    public SqlExecutor(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public boolean execute(String sql) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (Statement statement = connection.createStatement()) {
                return statement.execute(sql);
            }
        }
    }

    public <T> int write(PreparedSqlStatement<T> preparedSqlStatement) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(preparedSqlStatement.getSql())) {
                setParameters(statement, preparedSqlStatement.getParameters());
                return statement.executeUpdate();
            }
        }
    }

    public <T> List<T> read(PreparedSqlStatement<T> preparedSqlStatement) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(preparedSqlStatement.getSql())) {
                setParameters(statement, preparedSqlStatement.getParameters());
                try (ResultSet resultSet = statement.executeQuery()) {
                    return mapResultSet(resultSet, preparedSqlStatement.getTable());
                }
            }
        }
    }

    private <T> List<T> mapResultSet(ResultSet resultSet, Table<T> table) throws SQLException {
        List<T> results = new ArrayList<>();
        while (resultSet.next()) {
            results.add(mapRow(resultSet, table));
        }
        return results;
    }

    private <T> T mapRow(ResultSet resultSet, Table<T> table) throws SQLException {
        return TransientException.ends(() -> table.getColumns().stream().reduce(
            table.getEntityConstructor().get(),
            (entity, column) -> mapColumn(resultSet, entity, column),
            (e1, e2) -> e2
        ));
    }

    private <T, V> T mapColumn(ResultSet resultSet, T entity, Column<T, V> column) {
        return TransientException.starts(() -> column.set(entity, column.get(resultSet)));
    }

    private void setParameters(PreparedStatement statement, List<PreparedSqlStatement.Parameter<?>> parameters) throws SQLException {
        for (int i = 0; i < parameters.size(); i++) {
            setParameter(statement, i, parameters.get(i));
        }
    }

    private <T> void setParameter(PreparedStatement statement, int i, PreparedSqlStatement.Parameter<T> parameter) throws SQLException {
        parameter.getColumnAlias().getColumn().set(statement, i + 1, parameter.getValue());
    }

    private static class TransientException extends RuntimeException {

        @FunctionalInterface
        interface ThrowingSupplier<T> {

            T get() throws SQLException;

        }

        private final SQLException cause;

        private TransientException(SQLException cause) {
            super(cause);
            this.cause = cause;
        }

        static <T> T ends(Supplier<T> supplier) throws SQLException {
            try {
                return supplier.get();
            } catch (TransientException e) {
                throw e.cause;
            }
        }

        static <T> T starts(ThrowingSupplier<T> supplier) {
            try {
                return supplier.get();
            } catch (SQLException e) {
                throw new TransientException(e);
            }
        }

    }

}
