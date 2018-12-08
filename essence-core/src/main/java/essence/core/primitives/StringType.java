package essence.core.primitives;

import essence.core.basic.DataType;
import essence.core.random.RandomGenerator;
import essence.core.validation.SimpleValidationIssue;
import essence.core.validation.ValidationReporter;

import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static essence.core.primitives.Primitives.character;
import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.min;

public class StringType implements DataType<String> {

    private static final int MAX_VARIATION = 16;

    private final int minSize;
    private final int maxSize;
    private final int maxRandomSize;
    private final DataType<Character> characterType;
    private final BiFunction<String, Integer, String> cropper;
    private final BinaryOperator<String> extender;

    private StringType(int minSize, int maxSize, DataType<Character> characterType, BiFunction<String, Integer, String> cropper, BinaryOperator<String> extender) {
        this.minSize = minSize;
        this.maxSize = maxSize;
        this.maxRandomSize = min(maxSize, minSize + MAX_VARIATION);
        this.characterType = characterType;
        this.cropper = cropper;
        this.extender = extender;
    }

    private StringType(int minSize, int maxSize, DataType<Character> characterType, boolean leftSided) {
        this(minSize, maxSize, characterType,
            leftSided ? (string, size) -> string.substring(0, size) : (string, size) -> string.substring(string.length() - size),
            leftSided ? (string, ext) -> string + ext : (string, ext) -> ext + string
        );
    }

    public StringType() {
        this(0, MAX_VALUE, character, true);
    }

    public StringType rightSided() {
        return new StringType(minSize, maxSize, characterType, false);
    }

    public StringType leftSided() {
        return new StringType(minSize, maxSize, characterType, true);
    }

    public DataType<String> of(DataType<Character> characterType) {
        return of(minSize, maxSize, characterType);
    }

    public DataType<String> ofMin(int minSize, DataType<Character> characterType) {
        return of(minSize, maxSize, characterType);
    }

    public DataType<String> ofMax(int maxSize, DataType<Character> characterType) {
        return of(minSize, maxSize, characterType);
    }

    public DataType<String> of(int minSize, int maxSize, DataType<Character> characterType) {
        return new StringType(minSize, maxSize, characterType, cropper, extender);
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
    public String closestTo(String value, ValidationReporter reporter) {
        String valueWithValidChars = from(value.chars()
            .mapToObj(c -> (char) c)
            .map(c -> characterType.closestTo(c, reporter)));
        String validValue = valueWithValidChars;
        if (valueWithValidChars.length() < minSize) {
            reporter.report(new TooShortString(this, value));
            validValue = extender.apply(valueWithValidChars, from(Stream.generate(characterType::closestToIdentity)
                .limit(minSize - valueWithValidChars.length())));
        } else if (valueWithValidChars.length() > maxSize) {
            reporter.report(new TooLongString(this, value));
            validValue = cropper.apply(valueWithValidChars, maxSize);
        }
        return validValue;
    }

    private static String from(Stream<Character> characters) {
        return characters.collect(StringBuilder::new, (b, c) -> b.append(c.charValue()), StringBuilder::append).toString();
    }

    public int getMinSize() {
        return minSize;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public DataType<Character> getCharacterType() {
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
