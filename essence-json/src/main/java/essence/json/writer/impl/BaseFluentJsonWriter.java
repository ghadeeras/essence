package essence.json.writer.impl;

import essence.json.writer.ContextualFluentJsonWriter;
import essence.json.writer.impl.gen.OrthogonalJsonGenerator;
import essence.json.writer.impl.parts.GenericArrayWriter;
import essence.json.writer.impl.parts.GenericObjectWriter;

import java.math.BigDecimal;
import java.util.function.Consumer;

public abstract class BaseFluentJsonWriter<C> implements ContextualFluentJsonWriter<C> {

    protected final OrthogonalJsonGenerator generator;

    protected BaseFluentJsonWriter(OrthogonalJsonGenerator generator) {
        this.generator = generator;
    }

    protected abstract C context();

    @Override
    public C nullValue() {
        generator.writeNull();
        return context();
    }

    private <T> void write(T value, Consumer<T> writer) {
        if (value != null) {
            writer.accept(value);
        } else {
            generator.writeNull();
        }
    }

    @Override
    public C value(String value) {
        write(value, generator::write);
        return context();
    }

    @Override
    public C value(BigDecimal value) {
        write(value, generator::write);
        return context();
    }

    @Override
    public C value(Long value) {
        write(value, generator::write);
        return context();
    }

    @Override
    public C value(Integer value) {
        write(value, generator::write);
        return context();
    }

    @Override
    public C value(Boolean value) {
        write(value, generator::write);
        return context();
    }

    @Override
    public ObjectWriter<C> object() {
        generator.writeStartObject();
        return new GenericObjectWriter<>(generator.wrappedGenerator(), context());
    }

    @Override
    public ArrayWriter<C> array() {
        generator.writeStartArray();
        return new GenericArrayWriter<>(generator.wrappedGenerator(), context());
    }

}
