package essence.core.primitives;

import essence.core.basic.DataType;
import essence.core.random.RandomGenerator;
import essence.core.validation.ValidationReporter;

public class CharacterType implements DataType<Character> {

    @Override
    public Character identity() {
        return ' ';
    }

    @Override
    public Character randomValue(RandomGenerator generator) {
        return generator.nextChar(Character.MIN_VALUE, Character.MAX_VALUE);
    }

    @Override
    public Character closestTo(Character value, ValidationReporter reporter) {
        return value;
    }

}
