package essence.json.writer.impl.parts;

import essence.json.writer.ContextualFluentJsonWriter;
import jakarta.json.stream.JsonGenerator;

public class GenericObjectWriter<C> implements ContextualFluentJsonWriter.ObjectWriter<C> {

    private final JsonGenerator generator;
    private final C context;

    public GenericObjectWriter(JsonGenerator generator, C context) {
        this.generator = generator;
        this.context = context;
    }

    @Override
    public ContextualFluentJsonWriter<ContextualFluentJsonWriter.ObjectWriter<C>> in(String field) {
        return new GenericFieldWriter<>(generator, this, field);
    }

    @Override
    public C end() {
        generator.writeEnd();
        return context;
    }

}
