package essence.examples.main;

import essence.examples.model.Account;
import essence.json.reader.SchemaBasedJsonReader;
import essence.json.schema.JsonType;
import essence.json.writer.FluentJsonWriter;
import essence.json.writer.SchemaBasedJsonWriter;

import static essence.json.schema.JsonSchemaBuilder.jsonTypeFor;

public class Main {

    public static final JsonType<Account> accountJson = jsonTypeFor(Account.type);

    public static void main(String[] args) {
        Account account = accountPrototype();

        System.out.println(SchemaBasedJsonWriter.asString(accountJson, account));
        System.out.println(SchemaBasedJsonWriter.asString(accountJson, new Account()));
    }

    public static Account accountPrototype() {
        String json = FluentJsonWriter.json(writer -> writer
            .object()
                .in("accountNumber").value("1234567890")
                .in("balance").value(70000)
                .in("active").value(true)
                .in("owner").object()
                    .in("firstName").value("Ghadeer")
                    .in("lastName").value("AbouSaleh")
                .end()
                .in("transactions").array()
                    .object()
                        .in("transType").value("debit")
                        .in("amount").value(100000)
                    .end()
                    .object()
                        .in("transType").value("credit")
                        .in("amount").value(30000)
                    .end()
                .end()
            .end()
        );

        return SchemaBasedJsonReader.from(json).read(accountJson);
    }

}
