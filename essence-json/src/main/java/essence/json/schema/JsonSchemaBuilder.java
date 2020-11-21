package essence.json.schema;

import essence.core.basic.BaseCompositeType;
import essence.core.basic.DataType;
import essence.core.basic.Member;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class JsonSchemaBuilder {

    @SuppressWarnings("unchecked")
    public static <T> JsonType<T> jsonTypeFor(DataType<T> dataType) {
        JsonType<T> result = null;
        T value = dataType.identity();
        if (value instanceof String) {
            result = (JsonType<T>) string();
        } else if (value instanceof BigDecimal) {
            result = (JsonType<T>) decimal();
        } else if (value instanceof Long) {
            result = (JsonType<T>) longInteger();
        } else if (value instanceof Integer) {
            result = (JsonType<T>) integer();
        } else if (value instanceof Boolean) {
            result = (JsonType<T>) bool();
        } else if (dataType instanceof BaseCompositeType) {
            result = schemaFor((BaseCompositeType<T>) dataType);
        }
        return result;
    }

    public static <T> JsonObject<T> schemaFor(BaseCompositeType<T> compositeType) {
        Collection<JsonObject.Field<T, ?>> fields = compositeType.members().stream().map(member ->
            member.maxMultiplicity() == 1 ? fieldFor(member) : arrayFieldFor(member)
        ).collect(toList());
        return object(compositeType::construct, fields);
    }

    private static <T, C, V> JsonObject.Field<T, V> fieldFor(Member<T, C, V> member) {
        return field(member.name(), jsonTypeFor(member.type())).accessedBy(
            (T o) -> member.stream(member.of(o)).findFirst().orElse(null),
            (T o, V v) -> member.update(o, Stream.of(v).collect(member.collector()))
        );
    }

    private static <T, C, V> JsonObject.Field<T, List<V>> arrayFieldFor(Member<T, C, V> member) {
        return field(member.name(), arrayOf(jsonTypeFor(member.type()))).accessedBy(
            (T o) -> {
                C collection = member.of(o);
                return collection != null ? member.stream(collection).collect(toList()) : null;
            },
            (T o, List<V> v) -> member.update(o, v != null ? v.stream().collect(member.collector()) : null)
        );
    }

    public static JsonString string() {
        return new JsonString();
    }

    public static JsonDecimal decimal() {
        return new JsonDecimal();
    }

    public static JsonLong longInteger() {
        return new JsonLong();
    }

    public static JsonInteger integer() {
        return new JsonInteger();
    }

    public static JsonBoolean bool() {
        return new JsonBoolean();
    }

    public static <T> JsonArray<T> arrayOf(JsonType<T> itemType) {
        return new JsonArray<>(itemType);
    }

    @SafeVarargs
    public static <T> JsonObject<T> object(Supplier<T> constructor, JsonObject.Field<T, ?>... fields) {
        return new JsonObject<>(constructor, Stream.of(fields).collect(toList()));
    }

    public static <T> JsonObject<T> object(Supplier<T> constructor, Collection<JsonObject.Field<T, ?>> fields) {
        return new JsonObject<>(constructor, fields);
    }

    public static <V> AccessedBy<V> field(String name, JsonType<V> type) {
        return new AccessedBy<V>() {
            @Override
            public <T> JsonObject.Field<T, V> accessedBy(Function<T, V> getter, BiFunction<T, V, T> setter) {
                return new JsonObject.Field<>(name, type, getter, setter);
            }
        };
    }

    public interface AccessedBy<V> {

        <T> JsonObject.Field<T, V> accessedBy(Function<T, V> getter, BiFunction<T, V, T> setter);

        default <T> JsonObject.Field<T, V> accessedBy(Function<T, V> getter, BiConsumer<T, V> setter) {
            return accessedBy(getter, (o, v) -> {
                setter.accept(o, v);
                return o;
            });
        }

    }

}
