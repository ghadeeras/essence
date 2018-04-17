package essence.json.schema;

public class JsonString extends JsonType<String> {

    @Override
    public String from(JsonParsing parsing) {
        return parsing.recognize(this);
    }

    @Override
    public void to(JsonGeneration generation, String value) {
        generation.generate(value);
    }

}
