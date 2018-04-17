package essence.examples.rest.dao;

import essence.examples.model.Account;
import essence.jdbc.PreparedSqlStatement;
import essence.jdbc.SqlDialect;
import essence.jdbc.SqlExecutor;
import essence.jdbc.StandardSqlDialect;
import essence.jdbc.mapping.Table;
import essence.jdbc.mapping.columns.ColBigInteger;
import essence.jdbc.mapping.columns.ColDecimal;
import essence.jdbc.mapping.columns.ColFlag;
import essence.jdbc.mapping.columns.ColVarChar;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicLong;

import static essence.jdbc.Criterion.RHS.equalTo;
import static essence.jdbc.Criterion.where;

public class AccountDao {

    public static class AccountTable extends Table<Account> {

        private AccountTable() {
            super("account", Account.type::construct);
        }

        public ColBigInteger<Account> id =  mandatory(bigInteger()).mapsTo(Account.type.id);
        public ColVarChar<Account> accountNumber =  mandatory(varchar(16)).mapsTo(Account.type.accountNumber);
        public ColDecimal<Account> balance =  mandatory(decimal(20, 10)).mapsTo(Account.type.balance);
        public ColFlag<Account> active =  optional(flag()).mapsTo(Account.type.active);

    }

    private final SqlExecutor sqlExecutor;

    private final AccountTable accountTable = new AccountTable();
    private final SqlDialect sqlDialect = new StandardSqlDialect();
    private final AtomicLong sequence = new AtomicLong();

    public AccountDao(DataSource dataSource) throws SQLException {
        this.sqlExecutor = new SqlExecutor(dataSource);
        createTables();
    }

    private void createTables() throws SQLException {
        String createAccountTable = sqlDialect.create(accountTable);
        sqlExecutor.execute(createAccountTable);
    }

    public Account insert(Account account) throws SQLException {
        Account accountWithId = Account.type.id.update(account, sequence.incrementAndGet());
        PreparedSqlStatement<Account> statement = sqlDialect.insertInto(accountTable, accountWithId);
        int count = sqlExecutor.write(statement);
        return count > 0 ? accountWithId : account;
    }

    public int delete(Long id) throws SQLException {
        PreparedSqlStatement<Account> statement = sqlDialect.deleteFrom(accountTable, where(accountTable.id.aliased(), equalTo(id)));
        return sqlExecutor.write(statement);
    }

    public Account find(Long id) throws SQLException {
        PreparedSqlStatement<Account> statement = sqlDialect.selectFrom(accountTable, where(accountTable.id.aliased(), equalTo(id)));
        return sqlExecutor.read(statement).stream().findFirst().orElse(null);
    }

}
