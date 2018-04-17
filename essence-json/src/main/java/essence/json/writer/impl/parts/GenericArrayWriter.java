package essence.json.writer.impl.parts;

import essence.json.writer.ContextualFluentJsonWriter;
import essence.json.writer.impl.BaseFluentJsonWriter;
import essence.json.writer.impl.gen.RootJsonGenerator;

import javax.json.stream.JsonGenerator;

public class GenericArrayWriter<C> extends BaseFluentJsonWriter<ContextualFluentJsonWriter.ArrayWriter<C>> implements ContextualFluentJsonWriter.ArrayWriter<C> {

    private final C context;

    public GenericArrayWriter(JsonGenerator generator, C context) {
        super(new RootJsonGenerator(generator));
        this.context = context;
    }

    @Override
    protected ArrayWriter<C> context() {
        return this;
    }

    @Override
    public C end() {
        generator.wrappedGenerator().writeEnd();
        return context;
    }

}
