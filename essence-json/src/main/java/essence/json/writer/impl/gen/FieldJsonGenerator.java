package essence.json.writer.impl.gen;

import javax.json.stream.JsonGenerator;
import java.math.BigDecimal;

public class FieldJsonGenerator implements OrthogonalJsonGenerator {

    private final JsonGenerator generator;
    private final String field;

    public FieldJsonGenerator(JsonGenerator generator, String field) {
        this.generator = generator;
        this.field = field;
    }

    @Override
    public void writeNull() {
        generator.writeNull(field);
    }

    @Override
    public void write(String value) {
        generator.write(field, value);
    }

    @Override
    public void write(BigDecimal value) {
        generator.write(field, value);
    }

    @Override
    public void write(Long value) {
        generator.write(field, value);
    }

    @Override
    public void write(Integer value) {
        generator.write(field, value);
    }

    @Override
    public void write(Boolean value) {
        generator.write(field, value);
    }

    @Override
    public void writeStartObject() {
        generator.writeStartObject(field);
    }

    @Override
    public void writeStartArray() {
        generator.writeStartArray(field);
    }

    @Override
    public JsonGenerator wrappedGenerator() {
        return generator;
    }

}
