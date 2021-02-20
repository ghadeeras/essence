package essence.core.basic;

import essence.core.enumerables.OneOf;
import org.junit.Test;

import java.util.Comparator;
import java.util.List;

import static essence.core.basic.EnumerableDataTypesTest.SimpleColor;
import static essence.core.enumerables.Enumerables.enumeration;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.Assert.assertThat;

public class EnumerableDataTypesTest extends DataTypesTest<SimpleColor, OneOf<SimpleColor>> {

    public EnumerableDataTypesTest() {
        super(SimpleColor.type);
    }

    @Test
    public void testOrder() {
        var comparator = dataType.comparator();
        var orderedValues = dataType.orderedValues();
        for (var i = 1; i < orderedValues.size(); i++) {
            assertThat(comparator.compare(orderedValues.get(i), orderedValues.get(i - 1)), greaterThanOrEqualTo(0));
        }
    }

    enum SimpleColor {

        BLACK, WHITE, RED, GREEN, BLUE;

        static final OneOf<SimpleColor> type = enumeration(values());

    }

}
