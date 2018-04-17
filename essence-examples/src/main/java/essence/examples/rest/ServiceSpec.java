package essence.examples.rest;

import essence.core.basic.CompositeType;
import essence.core.basic.DataType;
import essence.core.marshalling.EndpointDefinition;
import essence.examples.model.Account;

import static essence.core.primitives.Primitives.longInteger;
import static essence.core.primitives.Primitives.truth;
import static essence.json.JsonFormat.jsonFormat;

public class ServiceSpec {

    public static final EndpointDefinition<Account, Account> createAccountEndPoint = EndpointDefinition
        .expects(Account.type).in(jsonFormat)
        .andReturns(Account.type).in(jsonFormat);

    public static final EndpointDefinition<Account, Account> saveAccountEndPoint = EndpointDefinition
        .expects(Account.type).in(jsonFormat)
        .andReturns(Account.type).in(jsonFormat);

    public static final EndpointDefinition<PrimitiveValue<Long>, PrimitiveValue<Boolean>> deleteAccountEndPoint = EndpointDefinition
        .expects(primitive(longInteger)).in(jsonFormat)
        .andReturns(primitive(truth)).in(jsonFormat);

    public static final EndpointDefinition<PrimitiveValue<Long>, Account> findAccountEndPoint = EndpointDefinition
        .expects(primitive(longInteger)).in(jsonFormat)
        .andReturns(Account.type).in(jsonFormat);

    public static class PrimitiveValue<T> {

        public static class Type<T> extends CompositeType<PrimitiveValue<T>> {

            public final One<T> value;

            public Type(DataType<T> type) {
                super(PrimitiveValue::new);
                this.value = mandatory(type).accessedBy(p -> p.value, setter((p, v) -> p.value = v));
            }

        }

        private T value;

    }

    public static <T> PrimitiveValue<T> wrap(T value) {
        PrimitiveValue<T> result = new PrimitiveValue<>();
        result.value = value;
        return result;
    }

    public static <T> T unwrap(PrimitiveValue<T> value) {
        return value.value;
    }

    private static <T> PrimitiveValue.Type<T> primitive(DataType<T> type) {
        return new PrimitiveValue.Type<>(type);
    }

}
