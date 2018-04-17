package essence.json;

import essence.core.basic.DataType;
import essence.core.marshalling.ContentFormat;
import essence.core.marshalling.Marshaller;
import essence.core.marshalling.Unmarshaller;
import essence.json.reader.SchemaBasedJsonReader;
import essence.json.schema.JsonSchemaBuilder;
import essence.json.schema.JsonType;
import essence.json.writer.SchemaBasedJsonWriter;

public class JsonFormat implements ContentFormat {

    private JsonFormat() {}

    @Override
    public <T> Marshaller<T> marshallerFor(DataType<T> dataType) {
        JsonType<T> jsonType = JsonSchemaBuilder.jsonTypeFor(dataType);
        return (value, writer) -> SchemaBasedJsonWriter.into(writer).write(jsonType, value);
    }

    @Override
    public <T> Unmarshaller<T> unmarshallerFor(DataType<T> dataType) {
        JsonType<T> jsonType = JsonSchemaBuilder.jsonTypeFor(dataType);
        return reader -> SchemaBasedJsonReader.from(reader).read(jsonType);
    }

    public static final JsonFormat jsonFormat = new JsonFormat();

}
