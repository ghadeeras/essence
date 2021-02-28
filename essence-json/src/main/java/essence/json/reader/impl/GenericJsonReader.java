package essence.json.reader.impl;

import essence.json.reader.SchemaBasedJsonReader;
import essence.json.reader.UnexpectedJsonTokenException;
import essence.json.schema.*;
import jakarta.json.Json;
import jakarta.json.stream.JsonParser;

import java.io.Reader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static jakarta.json.stream.JsonParser.Event;
import static jakarta.json.stream.JsonParser.Event.*;

public class GenericJsonReader implements SchemaBasedJsonReader, JsonParsing {

    private final JsonParser parser;

    private Event lookAhead;

    public GenericJsonReader(Reader reader) {
        this.parser = Json.createParser(reader);
    }

    private void lookAhead() {
        expectMore();
        lookAhead = parser.next();
    }

    private boolean found(Event event) {
        return lookAhead == event;
    }

    private void expectMore() {
        if (!parser.hasNext()) {
            throw new UnexpectedJsonTokenException();
        }
    }

    private void expect(Event expected) {
        if (!found(expected)) {
            throw new UnexpectedJsonTokenException();
        }
    }

    private void expectNumber(boolean integer) {
        expect(VALUE_NUMBER);
//        if (!parser.isIntegralNumber() ^ integer) {
//            throw new UnexpectedJsonTokenException();
//        }
    }

    @Override
    public String recognize(JsonString jsonString) {
        expect(VALUE_STRING);
        return parser.getString();
    }

    @Override
    public BigDecimal recognize(JsonDecimal jsonDecimal) {
        expectNumber(false);
        return parser.getBigDecimal();
    }

    @Override
    public Long recognize(JsonLong jsonLong) {
        expectNumber(true);
        return parser.getLong();
    }

    @Override
    public Integer recognize(JsonInteger jsonInteger) {
        expectNumber(true);
        return parser.getInt();
    }

    @Override
    public Boolean recognize(JsonBoolean jsonBoolean) {
        var result = found(VALUE_TRUE);
        if (!result) {
            expect(VALUE_FALSE);
        }
        return result;
    }

    @Override
    public <T> List<T> recognize(JsonArray<T> jsonArray) {
        List<T> result;
        expect(START_ARRAY);
        result = new ArrayList<>();
        lookAhead();
        while (!found(END_ARRAY)) {
            result.add(doRead(jsonArray.getItemType()));
            lookAhead();
        }
        return result;
    }

    @Override
    public <T> T recognize(JsonObject<T> jsonObject) {
        T result;
        expect(START_OBJECT);
        result = jsonObject.getConstructor().get();
        lookAhead();
        while (found(KEY_NAME)) {
            var key = parser.getString();
            var field = getExpectedField(jsonObject, key);
            result = read(field, result);
            lookAhead();
        }
        expect(END_OBJECT);
        return result;
    }

    private <T> JsonObject.Field<T, ?> getExpectedField(JsonObject<T> jsonObject, String key) {
        var field = jsonObject.getField(key);
        if (field == null) {
            throw new UnexpectedJsonTokenException();
        }
        return field;
    }

    private <T, V> T read(JsonObject.Field<T, V> field, T result) {
        return field.getSetter().apply(result, read(field.getType()));
    }

    @Override
    public <T> T read(JsonType<T> jsonType) {
        lookAhead();
        return doRead(jsonType);
    }

    private <T> T doRead(JsonType<T> jsonType) {
        return !found(VALUE_NULL) ? jsonType.from(this) : null;
    }

}
