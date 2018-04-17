package essence.examples.rest.server;

import essence.examples.rest.Service;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.function.Function;

import static essence.examples.rest.Profiling.profile;
import static essence.examples.rest.ServiceSpec.*;

@Path("/service")
public class RestService {

    private final Function<String, String> saveAccountFunction;
    private final Function<String, String> deleteAccountFunction;
    private final Function<String, String> findAccountFunction;

    public RestService(Service service) {
        saveAccountFunction = profile(saveAccountEndPoint.service(service::save));
        deleteAccountFunction = profile(deleteAccountEndPoint.service(id -> wrap(service.delete(unwrap(id)))));
        findAccountFunction = profile(findAccountEndPoint.service(id -> service.find(unwrap(id))));
    }

    @POST
    @Path("/save")
    public String save(String input) {
        return saveAccountFunction.apply(input);
    }

    @POST
    @Path("/delete")
    public String delete(String input) {
        return deleteAccountFunction.apply(input);
    }

    @POST
    @Path("/find")
    public String find(String input) {
        return findAccountFunction.apply(input);
    }

}
