package essence.core.validation;

import essence.core.basic.DataType;

public interface ValidationIssue<T> {

    DataType<T> dataType();

    T value();

    String message();

}
