package essence.examples.rest;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.function.Function;

import static essence.examples.rest.ServiceSpec.createAccountEndPoint;

@Path("/service")
public class RestService {

    private final Function<String, String> createAccountFunction;

    public RestService(Service service) {
        createAccountFunction = createAccountEndPoint.service(service::createAccount);
    }

    @POST
    @Path("/createAccount")
    public String createAccount(String input) {
        System.out.println("\nReceived:\n" + input);
        String result = createAccountFunction.apply(input);
        System.out.println("\nReturning:\n" + result);
        return result;
    }

}
