package helpers;

import api.models.RegisterRequest;
import api.models.UserCredentials;
import api.steps.AuthApi;
import net.datafaker.Faker;

public final class TestUsers {

    private static final Faker FAKER = new Faker();
    private static UserCredentials shared;

    private TestUsers() {
    }

    /**
     * Один общий пользователь на весь прогон: экономим rate-limit Habitica (30 req/min)
     * и не мусорим аккаунтами. Удаляется по завершении JVM.
     */
    public static synchronized UserCredentials shared() {
        if (shared == null) {
            shared = registerWithRateLimitRetry();
            UserCredentials toDelete = shared;
            Runtime.getRuntime().addShutdownHook(new Thread(() -> AuthApi.deleteUser(toDelete)));
        }
        return shared;
    }

    /**
     * Регистрация — первый запрос прогона: окно rate limit мог исчерпать
     * предыдущий запуск с этого же IP. Один повтор после полного окна.
     */
    private static UserCredentials registerWithRateLimitRetry() {
        try {
            return AuthApi.register(randomRegisterRequest());
        } catch (AssertionError firstAttempt) {
            try {
                Thread.sleep(61_000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return AuthApi.register(randomRegisterRequest());
        }
    }

    public static RegisterRequest randomRegisterRequest() {
        String username = "qa_" + FAKER.regexify("[a-z0-9]{10}");
        String password = FAKER.internet().password(10, 16, true, false, true);
        return RegisterRequest.builder()
                .username(username)
                .email(username + "@mailinator.com")
                .password(password)
                .confirmPassword(password)
                .build();
    }
}
