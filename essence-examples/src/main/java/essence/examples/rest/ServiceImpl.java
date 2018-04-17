package essence.examples.rest;

import static essence.examples.main.Main.Account;

public class ServiceImpl implements Service {

    @Override
    public Account createAccount(Account account) {
        return Account.type.accountNumber.randomize(account);
    }

}
