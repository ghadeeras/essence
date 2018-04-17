package essence.json.schema;

public abstract class JsonType<T> {

    public abstract T from(JsonParsing parsing);

    public abstract void to(JsonGeneration generation, T value);

}
