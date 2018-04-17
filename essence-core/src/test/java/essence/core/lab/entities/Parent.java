package essence.core.lab.entities;

import essence.core.basic.CompositeType;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class Parent {

    public static class Type extends CompositeType<Parent> {

        public final Many<Child> zeroToMany = setOf(0).toMany(Child.type)
                .accessedBy(p -> p.zeroToMany, setter((p, c) -> p.zeroToMany = c));

        public final Many<Child> oneToMany = setOf(1).toMany(Child.type)
                .accessedBy(p -> p.oneToMany, setter((p, c) -> p.oneToMany = c));

        public final OrderedMany<Child> orderedZeroToMany = listOf(0).toMany(Child.type)
                .accessedBy(p -> p.orderedZeroToMany, setter((p, c) -> p.orderedZeroToMany = c));

        public final OrderedMany<Child> orderedOneToMany = listOf(1).toMany(Child.type)
                .accessedBy(p -> p.orderedOneToMany, setter((p, c) -> p.orderedOneToMany = c));

        private Type() {
            super(Parent::new);
        }

    }

    public static final Type type = new Type();

    public Set<Child> zeroToMany = Collections.emptySet();
    public Set<Child> oneToMany = Collections.emptySet();

    public List<Child> orderedZeroToMany = Collections.emptyList();
    public List<Child> orderedOneToMany = Collections.emptyList();

}
