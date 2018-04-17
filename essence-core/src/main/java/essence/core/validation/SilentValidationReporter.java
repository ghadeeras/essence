package essence.core.validation;

public class SilentValidationReporter implements ValidationReporter {

    SilentValidationReporter() {
    }

    @Override
    public <T> void report(ValidationIssue<T> issue) {
    }

}
