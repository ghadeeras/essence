package essence.json.schema;

import java.util.Collection;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Collections.unmodifiableMap;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

public class JsonObject<T> extends JsonType<T> {

    private final Supplier<T> constructor;
    private final Map<String, Field<T, ?>> fields;

    public JsonObject(Supplier<T> constructor, Collection<Field<T, ?>> fields) {
        this.constructor = constructor;
        this.fields = unmodifiableMap(fields.stream().collect(toMap(Field::getName, identity())));
    }

    @Override
    public T from(JsonParsing parsing) {
        return parsing.recognize(this);
    }

    @Override
    public void to(JsonGeneration generation, T value) {
        generation.generate(this, value);
    }

    public Supplier<T> getConstructor() {
        return constructor;
    }

    public Collection<Field<T, ?>> getFields() {
        return fields.values();
    }

    public Field<T, ?> getField(String name) {
        return fields.get(name);
    }

    public static class Field<T, V> {

        private final String name;
        private final JsonType<V> type;
        private final Function<T, V> getter;
        private final BiFunction<T, V, T> setter;

        public Field(String name, JsonType<V> type, Function<T, V> getter, BiFunction<T, V, T> setter) {
            this.name = name;
            this.type = type;
            this.getter = getter;
            this.setter = setter;
        }

        public String getName() {
            return name;
        }

        public JsonType<V> getType() {
            return type;
        }

        public Function<T, V> getGetter() {
            return getter;
        }

        public BiFunction<T, V, T> getSetter() {
            return setter;
        }

    }

}
