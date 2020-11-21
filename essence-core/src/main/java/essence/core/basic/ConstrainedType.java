package essence.core.basic;

import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

public class ConstrainedType<T> extends BaseCompositeType<T> {

    public interface MemberDefiner<T> extends Consumer<ConstrainedType<T>> {}

    private class MemberRedefiner<TChild> implements MemberVisitor<T, TChild, Void> {

        @Override
        public Void visit(UniqueMember<T, TChild> member) {
            member(member, member.type(), member.minMultiplicity() > 0).accept(ConstrainedType.this);
            return null;
        }

        @Override
        public Void visit(SetMember<T, TChild> member) {
            member(member, member.type(), member.minMultiplicity(), member.maxMultiplicity()).accept(ConstrainedType.this);
            return null;
        }

        @Override
        public Void visit(ListMember<T, TChild> member) {
            member(member, member.type(), member.minMultiplicity(), member.maxMultiplicity()).accept(ConstrainedType.this);
            return null;
        }

    }

    private final BaseCompositeType<T> baseCompositeType;

    @SafeVarargs
    public ConstrainedType(BaseCompositeType<T> baseType, MemberDefiner<T>... definers) {
        super(baseType::construct);
        this.baseCompositeType = baseType;
        Stream.of(definers).forEach(definer -> definer.accept(this));
        Set<String> overridden = members().stream().map(Member::name).collect(toSet());
        baseType.members().stream()
            .filter(member -> !overridden.contains(member.name()))
            .forEach(this::redefine);
    }

    private <TContainer, TChild> void redefine(Member<T, TContainer, TChild> member) {
        MemberRedefiner<TChild> definer = new MemberRedefiner<>();
        member.accept(definer);
    }

    @Override
    public String name() {
        return baseCompositeType.name();
    }

    private static <T, TChild> MemberDefiner<T> member(UniqueMember<T, TChild> member, DataType<TChild> type, boolean mandatory) {
        return constrainedType -> constrainedType.define(new UniqueMember<>(
            m -> member.name(),
            type,
            constrainedType,
            mandatory,
            member.getter(),
            member.setter()
        ));
    }

    private static <T, TChild> MemberDefiner<T> member(SetMember<T, TChild> member, DataType<TChild> type, int minMultiplicity, int maxMultiplicitty) {
        return constrainedType -> constrainedType.define(new SetMember<>(
            m -> member.name(),
            type,
            constrainedType,
            minMultiplicity,
            maxMultiplicitty,
            member.getter(),
            member.setter()
        ));
    }

    private static <T, TChild> MemberDefiner<T> member(ListMember<T, TChild> member, DataType<TChild> type, int minMultiplicity, int maxMultiplicitty) {
        return constrainedType -> constrainedType.define(new ListMember<>(
            m -> member.name(),
            type,
            constrainedType,
            minMultiplicity,
            maxMultiplicitty,
            member.getter(),
            member.setter()
        ));
    }

    public static class UniqueAs<T, TChild> {

        private final UniqueMember<T, TChild> member;

        private UniqueAs(UniqueMember<T, TChild> member) {
            this.member = member;
        }

        public MemberDefiner<T> as(DataType<TChild> type) {
            return member(member, type, member.minMultiplicity() > 0);
        }

        public MemberDefiner<T> asMandatory(DataType<TChild> type) {
            return member(member, type, true);
        }

        public MemberDefiner<T> asOptional(DataType<TChild> type) {
            return member(member, type, false);
        }

    }

    public interface To<T, TChild> {

        MemberDefiner<T> to(int maxMultiplicity, DataType<TChild> type);

        default MemberDefiner<T> toMany(DataType<TChild> type) {
            return to(Integer.MAX_VALUE, type);
        }

    }

    public static class SetAs<T, TChild> {

        private final SetMember<T, TChild> member;

        private SetAs(SetMember<T, TChild> member) {
            this.member = member;
        }

        public MemberDefiner<T> as(DataType<TChild> type) {
            return member(member, type, member.minMultiplicity(), member.maxMultiplicity());
        }

        public To<T, TChild> as(int minMultiplicity) {
            return (maxMultiplicity, type) -> member(member, type, minMultiplicity, maxMultiplicity);
        }

    }

    public static class ListAs<T, TChild> {

        private final ListMember<T, TChild> member;

        private ListAs(ListMember<T, TChild> member) {
            this.member = member;
        }

        public MemberDefiner<T> as(DataType<TChild> type) {
            return member(member, type, member.minMultiplicity(), member.maxMultiplicity());
        }

        public To<T, TChild> as(int minMultiplicity) {
            return (maxMultiplicity, type) -> member(member, type, minMultiplicity, maxMultiplicity);
        }

    }

    public static <T, TChild> UniqueAs<T, TChild> member(UniqueMember<T, TChild> member) {
        return new UniqueAs<>(member);
    }

    public static <T, TChild> SetAs<T, TChild> member(SetMember<T, TChild> member) {
        return new SetAs<>(member);
    }

    public static <T, TChild> ListAs<T, TChild> member(ListMember<T, TChild> member) {
        return new ListAs<>(member);
    }

}
