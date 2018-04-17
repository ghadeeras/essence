package essence.json.schema;

import java.util.List;

public class JsonArray<T> extends JsonType<List<T>> {

    private final JsonType<T> itemType;

    public JsonArray(JsonType<T> itemType) {
        this.itemType = itemType;
    }

    @Override
    public List<T> from(JsonParsing parsing) {
        return parsing.recognize(this);
    }

    @Override
    public void to(JsonGeneration generation, List<T> value) {
        generation.generate(itemType, value);
    }

    public JsonType<T> getItemType() {
        return itemType;
    }

}
