package essence.examples.rest.client;

import essence.examples.model.Account;
import essence.examples.rest.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import static essence.examples.rest.Profiling.profile;
import static essence.examples.rest.ServiceSpec.*;

public class ServiceStub implements Service {

    private final Function<Account, Account> saveAccountFunction;
    private final Function<PrimitiveValue<Long>, PrimitiveValue<Boolean>> deleteAccountFunction;
    private final Function<PrimitiveValue<Long>, Account> findAccountFunction;

    private final HttpClient client;

    public ServiceStub() {
        this.saveAccountFunction = profile(saveAccountEndPoint.client(sender("save")));
        this.deleteAccountFunction = profile(deleteAccountEndPoint.client(sender("delete")));
        this.findAccountFunction = profile(findAccountEndPoint.client(sender("find")));

        this.client = HttpClient.newHttpClient();
    }

    private UnaryOperator<String> sender(String path) {
        return json -> doSend(path, json);
    }

    private String doSend(String path, String json) {
        System.out.println("\nSending:\n" + json);
        var request = HttpRequest.newBuilder(URI.create("http://localhost:8080/service/" + path))
            .header("Content-Type", "text/json")
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .build();
        try {
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            var result = response.body();
            System.out.println("\nReceived:\n" + result);
            return result;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
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
