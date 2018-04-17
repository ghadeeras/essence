package essence.json.writer;

import java.math.BigDecimal;
import java.util.function.Function;

public interface ContextualFluentJsonWriter<C> {

    C nullValue();

    C value(String value);

    C value(BigDecimal value);

    C value(Long value);

    C value(Integer value);

    C value(Boolean value);

    ObjectWriter<C> object();

    ArrayWriter<C> array();

    default C apply(Function<ContextualFluentJsonWriter<C>, C> writing) {
        return writing.apply(this);
    }

    interface ObjectWriter<C> {

        ContextualFluentJsonWriter<ObjectWriter<C>> in(String key);

        C end();

    }

    interface ArrayWriter<C> extends ContextualFluentJsonWriter<ArrayWriter<C>> {

        C end();

    }

}
