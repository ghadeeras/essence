package essence.examples.rest.client;

import essence.examples.model.Account;
import essence.examples.rest.Service;

import java.io.IOException;

public class Client {

    public static void main(String[] args) throws IOException {
        Service service = new ServiceStub();

        Account account = Account.type.randomValue();

        account = service.save(account);
        account = service.find(account.getId());
        service.delete(account.getId());
    }

}
