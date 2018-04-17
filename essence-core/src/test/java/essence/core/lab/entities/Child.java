package essence.core.lab.entities;

import essence.core.basic.CompositeType;

import static essence.core.primitives.Primitives.integer;
import static essence.core.primitives.Primitives.string;

public class Child {

    public static class Type extends CompositeType<Child> {

        public final One<Integer> mandatoryValue = mandatory(integer)
                .accessedBy(c -> c.mandatoryValue, setter((c, v) -> c.mandatoryValue = v));

        public final One<String> optionalValue = optional(string)
                .accessedBy(c -> c.optionalValue, setter((c, v) -> c.optionalValue = v));

        private Type() {
            super(Child::new);
        }

    }

    public static final Type type = new Type();

    public Integer mandatoryValue = 0;
    public String optionalValue = null;

}
