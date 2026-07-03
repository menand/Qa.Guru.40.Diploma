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
            shared = AuthApi.register(randomRegisterRequest());
            UserCredentials toDelete = shared;
            Runtime.getRuntime().addShutdownHook(new Thread(() -> AuthApi.deleteUser(toDelete)));
        }
        return shared;
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
