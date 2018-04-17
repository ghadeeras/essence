package essence.examples.rest.server;

import essence.examples.model.Account;
import essence.examples.rest.Service;
import essence.examples.rest.dao.AccountDao;

import java.sql.SQLException;

public class ServiceImpl implements Service {

    private final AccountDao dao;

    public ServiceImpl(AccountDao dao) {
        this.dao = dao;
    }

    @Override
    public Account save(Account account) {
        try {
            return dao.insert(account);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public Boolean delete(Long id) {
        try {
            return dao.delete(id) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public Account find(Long id) {
        try {
            return dao.find(id);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}
