package essence.examples.rest.client;

import essence.examples.model.Account;
import essence.examples.rest.Service;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import static essence.examples.rest.Profiling.profile;
import static essence.examples.rest.ServiceSpec.*;

public class ServiceStub implements Service {

    private final Function<Account, Account> saveAccountFunction;
    private final Function<PrimitiveValue<Long>, PrimitiveValue<Boolean>> deleteAccountFunction;
    private final Function<PrimitiveValue<Long>, Account> findAccountFunction;

    public ServiceStub() {
        this.saveAccountFunction = profile(saveAccountEndPoint.client(sender("save")));
        this.deleteAccountFunction = profile(deleteAccountEndPoint.client(sender("delete")));
        this.findAccountFunction = profile(findAccountEndPoint.client(sender("find")));
    }

    private static UnaryOperator<String> sender(String path) {
        WebTarget webTarget = ClientBuilder.newClient()
            .target("http://localhost:8080/service")
            .path(path);
        return json -> doSend(webTarget, json);
    }

    private static String doSend(WebTarget webTarget, String json) {
        System.out.println("\nSending:\n" + json);
        String result = webTarget
            .request(MediaType.TEXT_PLAIN_TYPE)
            .post(Entity.entity(json, MediaType.TEXT_PLAIN_TYPE))
            .readEntity(String.class);
        System.out.println("\nReceived:\n" + result);
        return result;
    }

    @Override
    public Account save(Account account) {
        return saveAccountFunction.apply(account);
    }

    @Override
    public Boolean delete(Long id) {
        return unwrap(deleteAccountFunction.apply(wrap(id)));
    }

    @Override
    public Account find(Long id) {
        return findAccountFunction.apply(wrap(id));
    }

}
