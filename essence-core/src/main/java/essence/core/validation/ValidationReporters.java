package essence.core.validation;

public class ValidationReporters {

    private ValidationReporters() {
    }

    public static final ValidationReporter failFast = new FailFastValidationReporter();
    public static final ValidationReporter silent = new SilentValidationReporter();

}
