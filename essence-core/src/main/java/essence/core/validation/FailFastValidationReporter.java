package essence.core.validation;

public class FailFastValidationReporter implements ValidationReporter {

    FailFastValidationReporter() {
    }

    @Override
    public <T> void report(ValidationIssue<T> issue) {
        throw new ValidationException(issue);
    }

}
