package essence.json.writer.impl;

import essence.json.schema.JsonGeneration;
import essence.json.schema.JsonObject;
import essence.json.schema.JsonType;
import essence.json.writer.SchemaBasedJsonWriter;
import essence.json.writer.impl.gen.FieldJsonGenerator;
import essence.json.writer.impl.gen.OrthogonalJsonGenerator;
import essence.json.writer.impl.gen.RootJsonGenerator;

import javax.json.Json;
import javax.json.stream.JsonGenerator;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class GenericJsonWriter implements SchemaBasedJsonWriter, JsonGeneration {

    private final JsonGenerator generator;

    private OrthogonalJsonGenerator currentGenerator;

    private static JsonGenerator generator(Writer writer) {
        Map<String, Object> configs = new HashMap<>();
        configs.put(JsonGenerator.PRETTY_PRINTING, true);
        return Json.createGeneratorFactory(configs).createGenerator(writer);
    }

    public GenericJsonWriter(Writer writer) {
        this.generator = generator(writer);
    }

    private void with(OrthogonalJsonGenerator generator, Runnable logic) {
        OrthogonalJsonGenerator oldGenerator = currentGenerator;
        currentGenerator = generator;
        logic.run();
        currentGenerator = oldGenerator;
    }

    private void inField(String field, Runnable logic) {
        with(new FieldJsonGenerator(generator, field), logic);
    }

    private void withoutFields(Runnable logic) {
        with(new RootJsonGenerator(generator), logic);
    }

    private <T> void write(T value, Consumer<T> writer) {
        if (value != null) {
            writer.accept(value);
        } else {
            currentGenerator.writeNull();
        }
    }

    @Override
    public <T> void write(JsonType<T> jsonType, T value) {
        withoutFields(() -> jsonType.to(this, value));
        generator.flush();
    }

    @Override
    public void generate(String value) {
        write(value, currentGenerator::write);
    }

    @Override
    public void generate(BigDecimal value) {
        write(value, currentGenerator::write);
    }

    @Override
    public void generate(Long value) {
        write(value, currentGenerator::write);
    }

    @Override
    public void generate(Integer value) {
        write(value, currentGenerator::write);
    }

    @Override
    public void generate(Boolean value) {
        write(value, currentGenerator::write);
    }

    @Override
    public <T> void generate(JsonType<T> type, Collection<T> values) {
        write(values, vs -> {
            currentGenerator.writeStartArray();
            doGenerate(type, vs);
            currentGenerator.writeEnd();
        });
    }

    private <T> void doGenerate(JsonType<T> type, Collection<T> values) {
        withoutFields(() ->
            values.forEach(value -> type.to(this, value))
        );
    }

    @Override
    public <T> void generate(JsonObject<T> type, T value) {
        write(value, v -> {
            currentGenerator.writeStartObject();
            doGenerate(type, v);
            currentGenerator.writeEnd();
        });
    }

    private <T> void doGenerate(JsonObject<T> type, T value) {
        type.getFields().forEach(field ->
            inField(field.getName(), () -> generateField(field, value))
        );
    }

    private <T, V> void generateField(JsonObject.Field<T, V> field, T value) {
        field.getType().to(this, field.getGetter().apply(value));
    }

}
