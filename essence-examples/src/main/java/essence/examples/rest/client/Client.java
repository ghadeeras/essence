package essence.examples.rest.client;

import essence.examples.model.Account;
import essence.examples.rest.Service;

public class Client {

    public static void main(String[] args) {
        Service service = new ServiceStub();

        var account = Account.type.randomValue();

        account = service.save(account);
        account = service.find(account.getId());
        service.delete(account.getId());
    }

}
