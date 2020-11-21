package essence.core.validation;

public class ValidationReporterWrapper implements ValidationReporter {

    private final ValidationReporter reporter;

    private boolean valid = true;

    ValidationReporterWrapper(ValidationReporter reporter) {
        this.reporter = reporter;
    }

    @Override
    public <T> void report(ValidationIssue<T> issue) {
        valid = false;
        reporter.report(issue);
    }

    public boolean isValid() {
        return valid;
    }

}
