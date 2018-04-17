package essence.jdbc.visitors;

import essence.jdbc.mapping.columns.*;

public interface ColumnVisitor<R> {

    <T> R visit(ColBigInteger<T> column);

    <T> R visit(ColDecimal<T> column);

    <T> R visit(ColFlag<T> column);

    <T> R visit(ColInteger<T> column);

    <T> R visit(ColVarChar<T> column);

}
