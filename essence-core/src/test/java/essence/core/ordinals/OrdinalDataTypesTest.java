package essence.core.ordinals;

import essence.core.basic.DataTypesTest;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public abstract class OrdinalDataTypesTest<T extends Comparable<T>, O extends OrdinalType<T, ?, O>> extends DataTypesTest<T, O> {

    public OrdinalDataTypesTest(O dataType) {
        super(dataType);
    }

    @Test
    public void testOnly() {
        var value = dataType.randomValue();
        var subType = dataType.only(value);

        assertThat(subType.randomValue(), equalTo(value));
        assertThat(subType.isValid(value), is(true));
    }

    @Test
    public void testExcept() {
        var value = dataType.randomValue();
        var subType = dataType.except(value);

        assertThat(subType.randomValue(), not(equalTo(value)));
        assertThat(subType.isValid(value), is(false));
    }

    @Test
    public void testOnlyComplementsExcept() {
        var value = dataType.randomValue();
        testComplement(dataType.only(value), dataType.except(value));
    }

    @Test
    public void testGreaterThan() {
        var value = dataType.randomValue();
        var subType = dataType.greaterThan(value);

        assertThat(subType.randomValue(), greaterThan(value));
    }

    @Test
    public void testLessThanOrEqualTo() {
        var value = dataType.randomValue();
        var subType = dataType.lessThanOrEqualTo(value);

        assertThat(subType.randomValue(), lessThanOrEqualTo(value));
    }

    @Test
    public void testGreaterThanComplementsLessThanOrEqualTo() {
        var value = dataType.randomValue();
        testComplement(dataType.greaterThan(value), dataType.lessThanOrEqualTo(value));
    }

    @Test
    public void testLessThan() {
        var value = dataType.randomValue();
        var subType = dataType.lessThan(value);

        assertThat(subType.randomValue(), lessThan(value));
    }

    @Test
    public void testGreaterThanOrEqualTo() {
        var value = dataType.randomValue();
        var subType = dataType.greaterThanOrEqualTo(value);

        assertThat(subType.randomValue(), greaterThanOrEqualTo(value));
    }

    @Test
    public void testLessThanComplementsGreaterThanOrEqualTo() {
        var value = dataType.randomValue();
        testComplement(dataType.lessThan(value), dataType.greaterThanOrEqualTo(value));
    }

    @Test
    public void testInInclusiveExclusive() {
        var values = randomOrderedValues(3);
        var subType = dataType.in(values.get(0), values.get(2));
        var ltType = dataType.lessThan(values.get(0));
        var gteType = dataType.greaterThanOrEqualTo(values.get(2));

        assertThat(subType.isValid(values.get(0)), is(true));
        assertThat(subType.isValid(values.get(1)), is(true));
        assertThat(subType.isValid(values.get(2)), is(false));
        assertThat(subType.randomValue(), allOf(
            greaterThanOrEqualTo(values.get(0)),
            lessThan(values.get(2))
        ));
        testNoOverlap(subType, ltType);
        testNoOverlap(subType, gteType);
    }

    @Test
    public void testUnion() {
        var values = randomOrderedValues(4);
        var t0 = dataType.lessThan(values.get(0));
        var t1 = dataType.in(values.get(0), values.get(1));
        var t2 = dataType.in(values.get(1), values.get(2));
        var t3 = dataType.in(values.get(2), values.get(3));
        var t4 = dataType.greaterThanOrEqualTo(values.get(3));

        var t12 = dataType.in(values.get(0), values.get(2));
        var t23 = dataType.in(values.get(1), values.get(3));
        var t13 = dataType.in(values.get(0), values.get(3));

        testComplement(t1.union(t3), t0.union(t2, t4));
        testEquivalence(t1.union(t2, t3), t13);
        testEquivalence(t12.union(t23), t13);
    }

    @Test
    public void testIntersect() {
        var values = randomOrderedValues(4);
        var t1 = dataType.in(values.get(0), values.get(1));
        var t2 = dataType.in(values.get(1), values.get(2));
        var t3 = dataType.in(values.get(2), values.get(3));

        var t12 = dataType.in(values.get(0), values.get(2));
        var t23 = dataType.in(values.get(1), values.get(3));

        assertThat(t1.intersect(t2).isEmpty(), is(true));
        assertThat(t1.intersect(t3).isEmpty(), is(true));
        testEquivalence(t12.intersect(t23), t2);
    }

    protected List<T> randomOrderedValues(int count) {
        var set = new TreeSet<T>();
        while (set.size() < count) {
            set.add(dataType.randomValue());
        }
        return new ArrayList<>(set);
    }

    protected void testComplement(O t1, O t2) {
        testNoOverlap(t1, t2);
        testEquivalence(t1, t2.complement());
        testEquivalence(t1.complement(), t2);
    }

}
