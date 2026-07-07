package api;

/**
 * Коды и тексты ошибок Habitica API (сверены с живыми ответами).
 * Меняются на бэкенде — правим в одном месте.
 */
public final class ApiErrors {

    public static final String NOT_AUTHORIZED = "NotAuthorized";
    public static final String NOT_FOUND = "NotFound";

    public static final String USERNAME_TAKEN = "Username already taken.";
    public static final String INVALID_CREDENTIALS =
            "Your email, username, or password are incorrect. Please try again or use \"Forgot Password.\"";
    public static final String MISSING_AUTH_HEADERS = "Missing authentication headers.";
    public static final String TASK_NOT_FOUND = "Task not found.";

    private ApiErrors() {
    }
}
