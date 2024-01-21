package essence.examples.model;

import essence.core.basic.CompositeType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static essence.core.primitives.Primitives.*;

public class Transaction {

    public static class Type extends CompositeType<Transaction> {

        public Type() {
            super(Transaction::new);
        }

        public final One<Long> id = mandatory(longInteger).accessedBy(Transaction::getId, setter(Transaction::setId));

        public final One<String> transType = mandatory(string).accessedBy(Transaction::getTransType, setter(Transaction::setTransType));
        public final One<BigDecimal> amount = mandatory(decimal).accessedBy(Transaction::getAmount, setter(Transaction::setAmount));

    }

    public static final Type type = new Type();

    private Long id;

    private String transType;
    private BigDecimal amount;
    private LocalDateTime dateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
