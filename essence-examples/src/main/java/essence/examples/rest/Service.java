package essence.examples.rest;

import essence.examples.model.Account;

public interface Service {

    Account save(Account account);

    Boolean delete(Long id);

    Account find(Long id);

}
