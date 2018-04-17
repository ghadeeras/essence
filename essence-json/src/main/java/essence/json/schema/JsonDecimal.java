package essence.json.schema;

import java.math.BigDecimal;

public class JsonDecimal extends JsonType<BigDecimal> {

    @Override
    public BigDecimal from(JsonParsing parsing) {
        return parsing.recognize(this);
    }

    @Override
    public void to(JsonGeneration generation, BigDecimal value) {
        generation.generate(value);
    }

}
