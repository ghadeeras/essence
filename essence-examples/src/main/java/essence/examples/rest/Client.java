package essence.examples.rest;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.function.UnaryOperator;

import static essence.examples.main.Main.Account;
import static essence.examples.main.Main.accountPrototype;

public class Client {

    public static void main(String[] args) throws IOException {
        Service service = new ServiceStub(Client::sender);

        Account account = service.createAccount(accountPrototype());
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

}
