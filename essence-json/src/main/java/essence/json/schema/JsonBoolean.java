package essence.json.schema;

public class JsonBoolean extends JsonType<Boolean> {

    @Override
    public Boolean from(JsonParsing parsing) {
        return parsing.recognize(this);
    }

    @Override
    public void to(JsonGeneration generation, Boolean value) {
        generation.generate(value);
    }

}
