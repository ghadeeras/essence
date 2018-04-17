package essence.core.marshalling;

import java.io.StringWriter;
import java.io.Writer;
import java.util.function.Function;

@FunctionalInterface
public interface Marshaller<T> extends Function<T, String> {

    void marshal(T value, Writer writer);

    default String apply(T value) {
        return marshal(value);
    }

    default String marshal(T value) {
        StringWriter writer = new StringWriter();
        marshal(value, writer);
        writer.flush();
        return writer.getBuffer().toString();
    }

}
