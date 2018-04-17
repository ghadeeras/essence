package essence.core.enumerables;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class OneOfTest {

    private OneOf<Integer> fibo = Enumerables.oneOf((i1, i2) -> i1 - i2, 1, 2, 3, 5, 8, 13, 21);

    @Test
    public void testClosest() {
        assertThat(fibo.closestTo(10), equalTo(8));
        assertThat(fibo.closestTo(11), equalTo(13));
        assertThat(fibo.closestTo(5), equalTo(5));
        assertThat(fibo.closestTo(0), equalTo(1));
        assertThat(fibo.closestTo(34), equalTo(21));
    }

    @Test
    public void testRandom() {
        assertThat(fibo.isValid(fibo.randomValue()), is(true));
    }

}
