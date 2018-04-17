package essence.json.reader;

import essence.json.reader.impl.GenericJsonReader;
import essence.json.schema.JsonType;

import java.io.Reader;
import java.io.StringReader;

public interface SchemaBasedJsonReader {

    static SchemaBasedJsonReader from(Reader reader) {
        return new GenericJsonReader(reader);
    }

    static SchemaBasedJsonReader from(String json) {
        return new GenericJsonReader(new StringReader(json));
    }

    <T> T read(JsonType<T> jsonType);

}
