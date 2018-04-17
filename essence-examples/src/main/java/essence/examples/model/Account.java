package essence.examples.model;

import essence.core.basic.CompositeType;

import java.math.BigDecimal;
import java.util.List;

import static essence.core.primitives.Primitives.*;

public class Account {

    public static class Type extends CompositeType<Account> {

        public Type() {
            super(Account::new);
        }

        public final One<Long> id = mandatory(longInteger).accessedBy(Account::getId, setter(Account::setId));

        public final One<String> accountNumber = mandatory(string).accessedBy(Account::getAccountNumber, setter(Account::setAccountNumber));
        public final One<BigDecimal> balance = mandatory(decimal).accessedBy(Account::getBalance, setter(Account::setBalance));
        public final One<Boolean> active = optional(truth).accessedBy(Account::isActive, setter(Account::setActive));

        public final One<Person> owner = mandatory(Person.type).accessedBy(Account::getOwner, setter(Account::setOwner));
        public final OrderedMany<Transaction> transactions = listOf(0).toMany(Transaction.type).accessedBy(Account::getTransactions, setter(Account::setTransactions));

    }

    public static final Type type = new Type();

    private Long id;

    private String accountNumber;
    private BigDecimal balance;
    private Boolean active;

    private Person owner;
    private List<Transaction> transactions;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Boolean isActive() {
        return active;
    }

    public void setActive(Boolean active) {
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
