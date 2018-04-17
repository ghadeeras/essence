package essence.core.marshalling;

import essence.core.basic.DataType;

public interface ContentFormat {

    <T> Marshaller<T> marshallerFor(DataType<T> dataType);

    <T> Unmarshaller<T> unmarshallerFor(DataType<T> dataType);

}
