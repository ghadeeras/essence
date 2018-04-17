package essence.json.writer;

import essence.json.schema.JsonType;
import essence.json.writer.impl.GenericJsonWriter;

import java.io.StringWriter;
import java.io.Writer;

public interface SchemaBasedJsonWriter {

    static SchemaBasedJsonWriter into(Writer writer) {
        return new GenericJsonWriter(writer);
    }

    static <T> String asString(JsonType<T> jsonType, T value) {
        StringWriter writer = new StringWriter();
        into(writer).write(jsonType, value);
        return writer.getBuffer().toString();
    }

    <T> void write(JsonType<T> jsonType, T value);

}
