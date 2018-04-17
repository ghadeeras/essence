package essence.core.basic;

import essence.core.utils.LazyValue;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class CompositeType<T> extends BaseCompositeType<T> {

    public class One<TChild> extends UniqueMember<T, TChild> {

        private One(DataType<TChild> type, boolean mandatory, Function<T, TChild> getter, BiFunction<T, TChild, T> setter) {
            super(memberName(), type, CompositeType.this, mandatory, getter, setter);
        }

    }

    public class Many<TChild> extends SetMember<T, TChild> {

        private Many(DataType<TChild> type, int minMultiplicity, int maxMultiplicity, Function<T, Set<TChild>> getter, BiFunction<T, Set<TChild>, T> setter) {
            super(memberName(), type, CompositeType.this, minMultiplicity, maxMultiplicity, getter, setter);
        }

    }

    public class OrderedMany<TChild> extends ListMember<T, TChild> {

        private OrderedMany(DataType<TChild> type, int minMultiplicity, int maxMultiplicity, Function<T, List<TChild>> getter, BiFunction<T, List<TChild>, T> setter) {
            super(memberName(), type, CompositeType.this, minMultiplicity, maxMultiplicity, getter, setter);
        }

    }

    private final LazyValue<Map<Member<?, ?, ?>, String>> memberNames = LazyValue.from(this::memberNames);

    public CompositeType(Supplier<T> constructor) {
        super(constructor);
    }

    private Map<Member<?, ?, ?>, String> memberNames() {
        Map<Member<?, ?, ?>, String> result = new HashMap<>();
        for (Field field : getClass().getFields()) {
            if (isMember(field)) {
                result.put(getMember(field), field.getName());
            }
        }
        return result;
    }

    private boolean isMember(Field field) {
        return Member.class.isAssignableFrom(field.getType());
    }

    private Member<?, ?, ?> getMember(Field field) {
        try {
            return (Member<?, ?, ?>) field.get(this);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Function<Member<?, ?, ?>, String> memberName() {
        return m -> memberNames.get().get(m);
    }

    private <C> One<C> one(DataType<C> type, boolean mandatory, Function<T, C> getter, BiFunction<T, C, T> setter) {
        return define(new One<>(type, mandatory, getter, setter));
    }

    private <C> Many<C> many(DataType<C> type, int minMultiplicity, int maxMultiplicity, Function<T, Set<C>> getter, BiFunction<T, Set<C>, T> setter) {
        return define(new Many<>(type, minMultiplicity, maxMultiplicity, getter, setter));
    }

    private <C> OrderedMany<C> orderedMany(DataType<C> type, int minMultiplicity, int maxMultiplicity, Function<T, List<C>> getter, BiFunction<T, List<C>, T> setter) {
        return define(new OrderedMany<>(type, minMultiplicity, maxMultiplicity, getter, setter));
    }

    protected static <TParent, TContainer> BiFunction<TParent, TContainer, TParent> setter(BiConsumer<TParent, TContainer> mutator) {
        return (parent, container) -> {
            mutator.accept(parent, container);
            return parent;
        };
    }

    @FunctionalInterface
    protected interface AccessedBy<TParent, TContainer, T, M extends Member<TParent, TContainer, T>> {

        M accessedBy(Function<TParent, TContainer> getter, BiFunction<TParent, TContainer, TParent> setter);

    }

    protected class SetMaxMultiplicity {

        private final int minMultiplicity;

        SetMaxMultiplicity(int minMultiplicity) {
            this.minMultiplicity = minMultiplicity;
        }

        public <C> AccessedBy<T, Set<C>, C, Many<C>> to(int maxMultiplicity, DataType<C> type) {
            return (getter, setter) -> many(type, minMultiplicity, maxMultiplicity, getter, setter);
        }

        public <C> AccessedBy<T, Set<C>, C, Many<C>> toMany(DataType<C> type) {
            return to(Integer.MAX_VALUE, type);
        }

    }

    protected class ListMaxMultiplicity {

        private final int minMultiplicity;

        ListMaxMultiplicity(int minMultiplicity) {
            this.minMultiplicity = minMultiplicity;
        }

        public <C> AccessedBy<T, List<C>, C, OrderedMany<C>> to(int maxMultiplicity, DataType<C> type) {
            return (getter, setter) -> orderedMany(type, minMultiplicity, maxMultiplicity, getter, setter);
        }

        public <C> AccessedBy<T, List<C>, C, OrderedMany<C>> toMany(DataType<C> type) {
            return to(Integer.MAX_VALUE, type);
        }

    }

    protected <C> AccessedBy<T, C, C, One<C>> optional(DataType<C> type) {
        return (getter, setter) -> one(type, false, getter, setter);
    }

    protected <C> AccessedBy<T, C, C, One<C>> mandatory(DataType<C> type) {
        return (getter, setter) -> one(type, true, getter, setter);
    }

    protected SetMaxMultiplicity setOf(int minMultiplicity) {
        return new SetMaxMultiplicity(minMultiplicity);
    }

    protected ListMaxMultiplicity listOf(int minMultiplicity) {
        return new ListMaxMultiplicity(minMultiplicity);
    }

}
