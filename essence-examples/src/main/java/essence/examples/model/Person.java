package essence.examples.model;

import essence.core.basic.CompositeType;

import static essence.core.primitives.Primitives.longInteger;
import static essence.core.primitives.Primitives.string;

class Person {

    public static class Type extends CompositeType<Person> {

        public Type() {
            super(Person::new);
        }

        public final One<Long> id = mandatory(longInteger).accessedBy(Person::getId, setter(Person::setId));

        public final One<String> firstName = mandatory(string).accessedBy(Person::getFirstName, setter(Person::setFirstName));
        public final One<String> lastName = mandatory(string).accessedBy(Person::getLastName, setter(Person::setLastName));

    }

    public static final Type type = new Type();

    private Long id;

    private String firstName;
    private String lastName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

}
