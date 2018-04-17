package essence.json.schema;

import java.math.BigDecimal;
import java.util.Collection;

public interface JsonGeneration {

    void generate(String value);

    void generate(BigDecimal value);

    void generate(Long value);

    void generate(Integer value);

    void generate(Boolean value);

    <T> void generate(JsonType<T> type, Collection<T> values);

    <T> void generate(JsonObject<T> type, T value);

}
