package essence.core.marshalling;

import java.io.Reader;
import java.io.StringReader;
import java.util.function.Function;

@FunctionalInterface
public interface Unmarshaller<T> extends Function<String, T> {

    T unmarshal(Reader reader);

    default T apply(String content) {
        return unmarshal(content);
    }

    default T unmarshal(String content) {
        Reader reader = new StringReader(content);
        return unmarshal(reader);
    }

}
