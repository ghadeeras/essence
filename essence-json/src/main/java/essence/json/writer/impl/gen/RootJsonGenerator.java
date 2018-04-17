package essence.json.writer.impl.gen;

import javax.json.stream.JsonGenerator;
import java.math.BigDecimal;

public class RootJsonGenerator implements OrthogonalJsonGenerator {

    private final JsonGenerator generator;

    public RootJsonGenerator(JsonGenerator generator) {
        this.generator = generator;
    }

    @Override
    public void writeNull() {
        generator.writeNull();
    }

    @Override
    public void write(String value) {
        generator.write(value);
    }

    @Override
    public void write(BigDecimal value) {
        generator.write(value);
    }

    @Override
    public void write(Long value) {
        generator.write(value);
    }

    @Override
    public void write(Integer value) {
        generator.write(value);
    }

    @Override
    public void write(Boolean value) {
        generator.write(value);
    }

    @Override
    public void writeStartObject() {
        generator.writeStartObject();
    }

    @Override
    public void writeStartArray() {
        generator.writeStartArray();
    }

    @Override
    public JsonGenerator wrappedGenerator() {
        return generator;
    }

}
