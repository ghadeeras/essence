package essence.core.basic;

import essence.core.testutils.SameSeed;
import org.junit.Rule;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public abstract class DataTypesTest<T, D extends DataType<T>> {

    protected final D dataType;

    @Rule
    public final SameSeed sameSeed = new SameSeed();

    public DataTypesTest(D dataType) {
        this.dataType = dataType;
    }

    @Test
    public void test() {
        var value = dataType.randomValue();
        assertThat(dataType.isValid(value), is(true));
    }

    protected void testEquivalence(DataType<T> t1, DataType<T> t2) {
        for (var i = 0; i < 100; i++) {
            assertTrue(t1.isValid(t2.randomValue()));
            assertTrue(t2.isValid(t1.randomValue()));
        }
    }

    protected void testNoOverlap(DataType<T> t1, DataType<T> t2) {
        for (var i = 0; i < 100; i++) {
            var t1Value = t1.randomValue();
            var t2Value = t2.randomValue();
            assertFalse(t1.isValid(t2Value));
            assertFalse(t2.isValid(t1Value));
        }
    }

}
