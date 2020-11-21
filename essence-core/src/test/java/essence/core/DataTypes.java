package essence.core;

import essence.core.basic.DataType;
import essence.core.enumerables.OneOf;
import org.junit.Test;

import java.util.Comparator;
import java.util.List;

import static essence.core.enumerables.Enumerables.enumeration;
import static essence.core.enumerables.Enumerables.oneOf;
import static essence.core.primitives.Primitives.*;
import static java.util.Comparator.naturalOrder;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.Assert.assertThat;

public class DataTypes {
    
    public static <T> void testDataType(DataType<T> dataType) {
        T value = dataType.randomValue();
        assertThat(dataType.isValid(value), is(true));
    }
    
    @Test
    public void primitiveTypes() {
        testDataType(truth);
        testDataType(integer);
        testDataType(longInteger);
        testDataType(decimal);
        testDataType(string);
    }

    public static <T> void testEnumerableDataType(OneOf<T> dataType) {
        testDataType(dataType);
        
        Comparator<T> comparator = dataType.comparator();
        List<T> orderedValues = dataType.orderedValues();
        assertThat(comparator.compare(dataType.randomValue(), dataType.identity()), greaterThanOrEqualTo(0));
        for (int i = 1; i < orderedValues.size(); i++) {
            assertThat(comparator.compare(orderedValues.get(i), orderedValues.get(i - 1)), greaterThanOrEqualTo(0));
        }
    }

    enum SimpleColor {
        
        BLACK, WHITE, RED, GREEN, BLUE;
        
        static OneOf<SimpleColor> type = enumeration(values());
        
    }

    static OneOf<Integer> fibonacci = oneOf(1, 2, 3, 5, 8, 13, 21).orderedBy(naturalOrder());

    @Test
    public void test_enumerable_types() {
        testEnumerableDataType(SimpleColor.type);
        testEnumerableDataType(fibonacci);
    }

}
