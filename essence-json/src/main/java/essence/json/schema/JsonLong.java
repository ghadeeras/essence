package essence.json.schema;

public class JsonLong extends JsonType<Long> {

    @Override
    public Long from(JsonParsing parsing) {
        return parsing.recognize(this);
    }

    @Override
    public void to(JsonGeneration generation, Long value) {
        generation.generate(value);
    }

}
