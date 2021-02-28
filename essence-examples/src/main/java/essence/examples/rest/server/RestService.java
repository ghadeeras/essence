package essence.examples.rest.server;

import essence.examples.rest.Service;

import java.util.function.Function;

import static essence.examples.rest.Profiling.profile;
import static essence.examples.rest.ServiceSpec.*;

public class RestService {

    public final Function<String, String> saveAccount;
    public final Function<String, String> deleteAccount;
    public final Function<String, String> findAccount;

    public RestService(Service service) {
        saveAccount = profile(saveAccountEndPoint.service(service::save));
        deleteAccount = profile(deleteAccountEndPoint.service(id -> wrap(service.delete(unwrap(id)))));
        findAccount = profile(findAccountEndPoint.service(id -> service.find(unwrap(id))));
    }

}
