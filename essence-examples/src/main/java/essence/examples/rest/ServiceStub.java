package essence.examples.rest;

import java.util.function.Function;
import java.util.function.UnaryOperator;

import static essence.examples.main.Main.Account;
import static essence.examples.rest.ServiceSpec.createAccountEndPoint;

public class ServiceStub implements Service {

    private final Function<Account, Account> createAccountFunction;

    public ServiceStub(Function<String, UnaryOperator<String>> senderFactory) {
        this.createAccountFunction = createAccountEndPoint.client(senderFactory.apply("createAccount"));
    }

    @Override
    public Account createAccount(Account input) {
        return createAccountFunction.apply(input);
    }

}
