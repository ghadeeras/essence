package essence.core.primitives;

import essence.core.ordinals.OrdinalType;
import essence.core.ordinals.Subset;
import essence.core.random.RandomGenerator;

public class LongType extends OrdinalType<Long, Long> {

    LongType() {
        super(null);
    }

    private LongType(Subset<Long> subset) {
        super(subset);
    }

    public LongType subType(SubsetConstructor<Long> constructor) {
        return new LongType(subSet(constructor));
    }

    @Override
    public Long identity() {
        return 0L;
    }

    @Override
    protected Long min() {
        return 1 + Long.MIN_VALUE / 2;
    }

    @Override
    protected Long max() {
        return -min();
    }

    @Override
    protected Long next(Long value) {
        return value + 1;
    }

    @Override
    protected Long prev(Long value) {
        return value - 1;
    }

    @Override
    protected Long add(Long value, Long distance) {
        return value + distance;
    }

    @Override
    protected Long randomDistance(RandomGenerator generator, Long range) {
        return generator.nextLong(0, range);
    }

    @Override
    protected Long distance(Long value1, Long value2) {
        return value1 > value2 ? value1 - value2 : value2 - value1;
    }

}
