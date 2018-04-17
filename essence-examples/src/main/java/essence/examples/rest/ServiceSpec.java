package essence.examples.rest;

import essence.core.marshalling.EndpointDefinition;

import static essence.examples.main.Main.Account;
import static essence.json.JsonFormat.jsonFormat;

public class ServiceSpec {

    public static final EndpointDefinition<Account, Account> createAccountEndPoint = EndpointDefinition
        .expects(Account.type).in(jsonFormat)
        .andReturns(Account.type).in(jsonFormat);

}
