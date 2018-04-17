package essence.json.writer.impl.gen;

import javax.json.stream.JsonGenerator;
import java.math.BigDecimal;

public interface OrthogonalJsonGenerator {

    void writeNull();

    void write(String value);

    void write(BigDecimal value);

    void write(Long value);

    void write(Integer value);

    void write(Boolean value);

    void writeStartObject();

    void writeStartArray();

    default void writeEnd() {
        wrappedGenerator().writeEnd();
    }

    JsonGenerator wrappedGenerator();

}
