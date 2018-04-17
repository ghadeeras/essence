package essence.json.schema;

import java.math.BigDecimal;
import java.util.List;

public interface JsonParsing {

    String recognize(JsonString jsonString);

    BigDecimal recognize(JsonDecimal jsonDecimal);

    Long recognize(JsonLong jsonLong);

    Integer recognize(JsonInteger jsonInteger);

    Boolean recognize(JsonBoolean jsonBoolean);

    <T> List<T> recognize(JsonArray<T> jsonArray);

    <T> T recognize(JsonObject<T> jsonObject);

}
