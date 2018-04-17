package essence.json.writer;

import essence.json.writer.impl.GenericFluentJsonWriter;

import java.io.StringWriter;
import java.io.Writer;
import java.util.function.Function;

public interface FluentJsonWriter extends ContextualFluentJsonWriter<FluentJsonWriter.Flusher> {

    static FluentJsonWriter instance(Writer writer) {
        return new GenericFluentJsonWriter(writer);
    }

    static String json(Function<FluentJsonWriter, Flusher> jsonWritingLogic) {
        StringWriter writer = new StringWriter();
        jsonWritingLogic.apply(instance(writer)).flush();
        return writer.getBuffer().toString();
    }

    interface Flusher {

        void flush();

    }

}
