package essence.core.primitives;

import essence.core.ordinals.OrdinalType;
import essence.core.ordinals.Subset;
import essence.core.random.RandomGenerator;

public class CharacterType extends OrdinalType<Character, Integer> {

    CharacterType() {
        super(null);
    }

    private CharacterType(Subset<Character> subset) {
        super(subset);
    }

    public CharacterType subType(SubsetConstructor<Character> constructor) {
        return new CharacterType(subSet(constructor));
    }

    @Override
    public Character identity() {
        return ' ';
    }

    @Override
    protected Character min() {
        return Character.MIN_VALUE;
    }

    @Override
    protected Character max() {
        return Character.MAX_VALUE;
    }

    @Override
    protected Character next(Character value) {
        return add(value, 1);
    }

    @Override
    protected Character prev(Character value) {
        return add(value, -1);
    }

    @Override
    protected Character add(Character value, Integer distance) {
        return (char) (value + distance);
    }

    @Override
    protected Integer randomDistance(RandomGenerator generator, Integer range) {
        return generator.nextInt(0, range);
    }

    @Override
    protected Integer distance(Character value1, Character value2) {
        int i1 = value1;
        int i2 = value2;
        return i1 > i2 ? i1 - i2 : i2 - i1;
    }

}
