package helpers;

import api.models.RegisterRequest;
import api.models.UserCredentials;
import api.steps.AuthApi;
import api.steps.UserApi;
import net.datafaker.Faker;

public final class TestUsers {

    public static final String EMAIL_DOMAIN = "@mailinator.com";

    private static final Faker FAKER = new Faker();
    private static UserCredentials shared;
    private static boolean welcomed;

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

    /** Общий пользователь без приветственного онбординга — для браузерных и мобильных тестов. */
    public static synchronized UserCredentials sharedWelcomed() {
        UserCredentials user = shared();
        if (!welcomed) {
            UserApi.markWelcomed(user);
            welcomed = true;
        }
        return user;
    }

    /**
     * Регистрация — первый запрос прогона: окно rate limit мог исчерпать
     * предыдущий запуск с этого же IP. Один повтор после полного окна.
     */
    private static UserCredentials registerWithRateLimitRetry() {
        try {
            return AuthApi.register(randomRegisterRequest());
        } catch (AssertionError firstAttempt) {
            if (!String.valueOf(firstAttempt.getMessage()).contains("429")) {
                throw firstAttempt;
            }
            try {
                Thread.sleep(61_000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            try {
                return AuthApi.register(randomRegisterRequest());
            } catch (AssertionError secondAttempt) {
                secondAttempt.addSuppressed(firstAttempt);
                throw secondAttempt;
            }
        }
    }

    public static RegisterRequest randomRegisterRequest() {
        String username = "qa_" + FAKER.regexify("[a-z0-9]{10}");
        String password = FAKER.internet().password(10, 16, true, false, true);
        return RegisterRequest.builder()
                .username(username)
                .email(username + EMAIL_DOMAIN)
                .password(password)
                .confirmPassword(password)
                .build();
    }
}
