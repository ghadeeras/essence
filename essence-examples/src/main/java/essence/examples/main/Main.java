package essence.examples.main;

import essence.core.basic.CompositeType;
import essence.json.reader.SchemaBasedJsonReader;
import essence.json.schema.JsonType;
import essence.json.writer.FluentJsonWriter;
import essence.json.writer.SchemaBasedJsonWriter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static essence.core.primitives.Primitives.*;
import static essence.json.schema.JsonSchemaBuilder.jsonTypeFor;

public class Main {

    public static final JsonType<Account> accountJson = jsonTypeFor(Account.type);

    public static void main(String[] args) {
        Account account = accountPrototype();

        System.out.println("Synthesizing ...");
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

    public static class Account {

        public static class Type extends CompositeType<Account> {

            public Type() {
                super(Account::new);
            }

            public final One<String> accountNumber = mandatory(string).accessedBy(Account::getAccountNumber, setter(Account::setAccountNumber));
            public final One<BigDecimal> balance = mandatory(decimal).accessedBy(Account::getBalance, setter(Account::setBalance));
            public final One<Boolean> active = optional(truth).accessedBy(Account::isActive, setter(Account::setActive));

            public final One<Person> owner = mandatory(Person.type).accessedBy(Account::getOwner, setter(Account::setOwner));
            public final OrderedMany<Transaction> transactions = listOf(0).toMany(Transaction.type).accessedBy(Account::getTransactions, setter(Account::setTransactions));

        }

        public static final Type type = new Type();

        private String accountNumber;
        private BigDecimal balance;
        private boolean active;

        private Person owner;
        private List<Transaction> transactions;

        public String getAccountNumber() {
            return accountNumber;
        }

        public void setAccountNumber(String accountNumber) {
            this.accountNumber = accountNumber;
        }

        public BigDecimal getBalance() {
            return balance;
        }

        public void setBalance(BigDecimal balance) {
            this.balance = balance;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        public Person getOwner() {
            return owner;
        }

        public void setOwner(Person owner) {
            this.owner = owner;
        }

        public List<Transaction> getTransactions() {
            return transactions;
        }

        public void setTransactions(List<Transaction> transactions) {
            this.transactions = transactions;
        }

    }

    private static class Person {

        public static class Type extends CompositeType<Person> {

            public Type() {
                super(Person::new);
            }

            public final One<String> firstName = mandatory(string).accessedBy(Person::getFirstName, setter(Person::setFirstName));
            public final One<String> lastName = mandatory(string).accessedBy(Person::getLastName, setter(Person::setLastName));

        }

        public static final Type type = new Type();

        private String firstName;
        private String lastName;

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

    }

    private static class Transaction {

        public static class Type extends CompositeType<Transaction> {

            public Type() {
                super(Transaction::new);
            }

            public final One<String> transType = mandatory(string).accessedBy(Transaction::getTransType, setter(Transaction::setTransType));
            public final One<BigDecimal> amount = mandatory(decimal).accessedBy(Transaction::getAmount, setter(Transaction::setAmount));

        }

        public static final Type type = new Type();

        private String transType;
        private BigDecimal amount;
        private LocalDateTime dateTime;

        public String getTransType() {
            return transType;
        }

        public void setTransType(String transType) {
            this.transType = transType;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }

        public LocalDateTime getDateTime() {
            return dateTime;
        }

        public void setDateTime(LocalDateTime dateTime) {
            this.dateTime = dateTime;
        }

    }

}
