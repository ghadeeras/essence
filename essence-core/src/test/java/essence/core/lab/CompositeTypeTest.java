package essence.core.lab;

import essence.core.basic.Member;
import essence.core.lab.entities.Child;
import essence.core.lab.entities.Parent;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static essence.core.basic.ConstrainedType.member;
import static essence.core.enumerables.Enumerables.oneOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CompositeTypeTest {

    @Test
    public void test() {
        Parent.Type p = Parent.type;
        Child.Type c = Child.type;

        Parent parent = Parent.type.randomValue();
        Parent anotherParent = Parent.type.with(
            member(p.oneToMany).as(1).to(2, Child.type.with(
                member(c.optionalValue).asMandatory(oneOf("koko", "bobo", "soso"))
            ))
        ).randomValue();

        List<Member<Parent, ?, ?>> members = p.getMembers();
        System.out.println(members.stream().map(Member::getName).collect(Collectors.toList()));

        assertThat(p.isValid(parent), is(true));
        assertThat(p.isValid(anotherParent), is(true));
    }

}
