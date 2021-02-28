package essence.json.writer.impl.parts;

import essence.json.writer.ContextualFluentJsonWriter;
import essence.json.writer.impl.BaseFluentJsonWriter;
import essence.json.writer.impl.gen.FieldJsonGenerator;
import jakarta.json.stream.JsonGenerator;

public class GenericFieldWriter<C> extends BaseFluentJsonWriter<ContextualFluentJsonWriter.ObjectWriter<C>> {

    private final ContextualFluentJsonWriter.ObjectWriter<C> objectWriter;

    public GenericFieldWriter(JsonGenerator generator, ContextualFluentJsonWriter.ObjectWriter<C> objectWriter, String field) {
        super(new FieldJsonGenerator(generator, field));
        this.objectWriter = objectWriter;
    }

    @Override
    protected ContextualFluentJsonWriter.ObjectWriter<C> context() {
        return objectWriter;
    }

}
