package essence.core.validation;

public interface ValidationReporter {

    <T> void report(ValidationIssue<T> issue);

}
