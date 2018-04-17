package essence.json.writer.impl;

import essence.json.writer.FluentJsonWriter;
import essence.json.writer.impl.gen.RootJsonGenerator;

import javax.json.Json;
import javax.json.stream.JsonGenerator;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public class GenericFluentJsonWriter extends BaseFluentJsonWriter<FluentJsonWriter.Flusher> implements FluentJsonWriter {

    public GenericFluentJsonWriter(Writer writer) {
        super(new RootJsonGenerator(generator(writer)));
    }

    private static JsonGenerator generator(Writer writer) {
        Map<String, Object> configs = new HashMap<>();
        configs.put(JsonGenerator.PRETTY_PRINTING, true);
        return Json.createGeneratorFactory(configs).createGenerator(writer);
    }

    @Override
    protected Flusher context() {
        return () -> generator.wrappedGenerator().flush();
    }

    public static void main(String[] args) {
        generator(new PrintWriter(System.out)).write("").flush();
    }

}
