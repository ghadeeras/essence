package essence.core.validation;

public class ValidationException extends RuntimeException {

    private final ValidationIssue<?> issue;

    public ValidationException(ValidationIssue<?> issue) {
        this.issue = issue;
    }

    @Override
    public String getMessage() {
        return issue.message();
    }

    public ValidationIssue<?> getIssue() {
        return issue;
    }

}
