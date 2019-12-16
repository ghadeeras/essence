package essence.core;

import essence.core.basic.DataType;
import essence.core.enumerables.OneOf;
import org.junit.Test;

import java.util.Comparator;
import java.util.List;

import static essence.core.enumerables.Enumerables.enumeration;
import static essence.core.enumerables.Enumerables.oneOf;
import static essence.core.primitives.Primitives.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.Assert.assertThat;

public class DataTypes {
    
    public static <T> void testDataType(DataType<T> dataType) {
        T value = dataType.randomValue();
        assertThat(dataType.isValid(value), is(true));
        assertThat(dataType.closestTo(value), equalTo(value));
        
        assertThat(dataType.isValid(dataType.closestToIdentity()), is(true));
        assertThat(dataType.closestToIdentity(), equalTo(dataType.closestTo(dataType.identity())));
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

    static OneOf<Integer> fibonacci = oneOf(1, 2, 3, 5, 8, 13, 21).orderedBy((i1, i2) -> i1 - i2);

    @Test
    public void enumerables() {
        testEnumerableDataType(SimpleColor.type);
        testEnumerableDataType(fibonacci);

        assertThat(fibonacci.closestTo(10), equalTo(8));
        assertThat(fibonacci.closestTo(11), equalTo(13));
        assertThat(fibonacci.closestTo(5), equalTo(5));
        assertThat(fibonacci.closestTo(0), equalTo(1));
        assertThat(fibonacci.closestTo(34), equalTo(21));
    }

}
