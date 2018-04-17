package essence.json.schema;

public class JsonInteger extends JsonType<Integer> {

    @Override
    public Integer from(JsonParsing parsing) {
        return parsing.recognize(this);
    }

    @Override
    public void to(JsonGeneration generation, Integer value) {
        generation.generate(value);
    }

}
