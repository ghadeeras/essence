package essence.core.utils;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

public class MemberNaming<T, M> implements Function<M, String> {

    private final T owner;
    private final Class<M> memberType;

    private final LazyValue<Map<M, String>> memberNames = LazyValue.from(this::memberNames);

    public MemberNaming(T owner, Class<M> memberType) {
        this.owner = owner;
        this.memberType = memberType;
    }

    private Map<M, String> memberNames() {
        return Stream.of(owner.getClass().getFields())
            .filter(this::isMember)
            .collect(toMap(this::getMember, Field::getName));
    }

    private boolean isMember(Field field) {
        return memberType.isAssignableFrom(field.getType());
    }

    private M getMember(Field field) {
        try {
            return memberType.cast(field.get(owner));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String apply(M member) {
        return memberNames.get().get(member);
    }

    public LazyValue<String> lazily(M member) {
        return LazyValue.from(() -> apply(member));
    }

}
