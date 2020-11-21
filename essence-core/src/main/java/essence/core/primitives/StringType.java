package essence.core.primitives;

import essence.core.basic.DataType;
import essence.core.random.RandomGenerator;
import essence.core.validation.SimpleValidationIssue;
import essence.core.validation.ValidationReporter;

import java.util.function.Supplier;

import static essence.core.primitives.Primitives.character;
import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.min;

public class StringType implements DataType<String> {

    private static final int MAX_VARIATION = 16;

    private final int minSize;
    private final int maxSize;
    private final int maxRandomSize;
    private final DataType<Character> characterType;

    private StringType(int minSize, int maxSize, DataType<Character> characterType) {
        this.minSize = minSize;
        this.maxSize = maxSize;
        this.maxRandomSize = min(maxSize, minSize + MAX_VARIATION);
        this.characterType = characterType;
    }

    public StringType() {
        this(0, MAX_VALUE, character);
    }

    public StringType of(DataType<Character> characterType) {
        return of(minSize, maxSize, characterType);
    }

    public StringType ofMin(int minSize, DataType<Character> characterType) {
        return of(minSize, maxSize, characterType);
    }

    public StringType ofMax(int maxSize, DataType<Character> characterType) {
        return of(minSize, maxSize, characterType);
    }

    public StringType of(int minSize, int maxSize, DataType<Character> characterType) {
        return new StringType(minSize, maxSize, characterType);
    }

    @Override
    public String identity() {
        return "";
    }

    @Override
    public String randomValue(RandomGenerator generator) {
        Supplier<Character> characterSupplier = () -> characterType.randomValue(generator);
        return generator.nextString(minSize, maxRandomSize, characterSupplier);
    }

    @Override
    public void validate(String value, ValidationReporter reporter) {
        value.chars().forEach(c -> characterType.validate((char) c, reporter));
        if (value.length() < minSize) {
            reporter.report(new TooShortString(this, value));
        } else if (value.length() > maxSize) {
            reporter.report(new TooLongString(this, value));
        }
    }

    public int getMinSize() {
        return minSize;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public DataType<Character> characterType() {
        return characterType;
    }

    public static class InvalidString extends SimpleValidationIssue<String> {

        private final StringType stringType;

        private InvalidString(StringType stringType, String value, Supplier<String> message) {
            super(stringType, value, message);
            this.stringType = stringType;
        }

        public StringType stringType() {
            return stringType;
        }

    }

    public static class TooShortString extends InvalidString {

        private TooShortString(StringType stringType, String value) {
            super(stringType, value, () -> "String '" + value + "' is shorter than the minimum size: " + stringType.getMinSize());
        }

    }

    public static class TooLongString extends InvalidString {

        private TooLongString(StringType stringType, String value) {
            super(stringType, value, () -> "String '" + value + "' is longer than the maximum size: " + stringType.getMaxSize());
        }

    }

}
