package essence.core.basic;

public interface MemberVisitor<TParent, T, R> {

    R visit(UniqueMember<TParent, T> member);
    R visit(SetMember<TParent, T> member);
    R visit(ListMember<TParent, T> member);

}
