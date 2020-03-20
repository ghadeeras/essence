package essence.core.primitives;

import essence.core.ordinals.OrdinalType;
import essence.core.ordinals.Subset;
import essence.core.random.RandomGenerator;

public class IntegerType extends OrdinalType<Integer, Long> {

    IntegerType() {
        super(null);
    }

    private IntegerType(Subset<Integer> subset) {
        super(subset);
    }

    public IntegerType subType(SubsetConstructor<Integer> constructor) {
        return new IntegerType(subSet(constructor));
    }

    @Override
    public Integer identity() {
        return 0;
    }

    @Override
    protected Integer min() {
        return Integer.MIN_VALUE;
    }

    @Override
    protected Integer max() {
        return Integer.MAX_VALUE;
    }

    @Override
    protected Integer next(Integer value) {
        return value + 1;
    }

    @Override
    protected Integer prev(Integer value) {
        return value - 1;
    }

    @Override
    protected Integer add(Integer value, Long distance) {
        return (int) (value + distance);
    }

    @Override
    protected Long randomDistance(RandomGenerator generator, Long range) {
        return generator.nextLong(0, range);
    }

    @Override
    protected Long distance(Integer value1, Integer value2) {
        long l1 = value1;
        long l2 = value2;
        return l1 > l2 ? l1 - l2 : l2 - l1;
    }
}
