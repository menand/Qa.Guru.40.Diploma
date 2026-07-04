package tests.api;

import api.models.ErrorResponse;
import api.models.RegisterRequest;
import api.models.UserCredentials;
import api.steps.AuthApi;
import api.steps.UserApi;
import helpers.TestUsers;
import io.qameta.allure.Feature;
import io.qameta.allure.Link;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;

@Feature("Аутентификация")
@Link(name = "Habitica API doc", url = "https://habitica.com/apidoc/")
public class AuthApiTests extends ApiTestBase {

    @Test
    @Story("Регистрация")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Успешная регистрация нового пользователя возвращает id и apiToken")
    void successfulRegistrationReturnsCredentials() {
        RegisterRequest request = TestUsers.randomRegisterRequest();

        UserCredentials created = AuthApi.register(request);

        try {
            step("Проверить, что id и apiToken — валидные UUID", () -> {
                assertThat(created.getId()).matches(UUID_REGEX);
                assertThat(created.getApiToken()).matches(UUID_REGEX);
            });
        } finally {
            step("Очистка: удалить созданный аккаунт", () -> AuthApi.deleteUser(created));
        }
    }

    @Test
    @Story("Регистрация")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Регистрация с занятым username отклоняется с 401 NotAuthorized")
    void registrationWithTakenUsernameFails() {
        RegisterRequest request = TestUsers.randomRegisterRequest()
                .toBuilder()
                .username(USER.getUsername())
                .build();

        ErrorResponse error = AuthApi.registerExpectingError(request, 401);

        step("Проверить ответ об ошибке", () -> {
            assertThat(error.getSuccess()).isFalse();
            assertThat(error.getError()).isEqualTo("NotAuthorized");
            assertThat(error.getMessage()).isEqualTo("Username already taken.");
        });
    }

    @Test
    @Story("Логин")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Логин с валидными кредами возвращает id и apiToken пользователя")
    void successfulLoginReturnsToken() {
        UserCredentials loggedIn = AuthApi.login(USER.getUsername(), USER.getPassword());

        step("Проверить учётные данные в ответе", () -> {
            assertThat(loggedIn.getId()).isEqualTo(USER.getId());
            assertThat(loggedIn.getApiToken()).isEqualTo(USER.getApiToken());
        });
    }

    @Test
    @Story("Логин")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Логин с неверным паролем отклоняется с 401 NotAuthorized")
    void loginWithWrongPasswordFails() {
        ErrorResponse error = AuthApi.loginExpectingError(USER.getUsername(), "wrong-password-123", 401);

        step("Проверить ответ об ошибке", () -> {
            assertThat(error.getError()).isEqualTo("NotAuthorized");
            assertThat(error.getMessage()).isEqualTo(
                    "Your email, username, or password are incorrect. "
                            + "Please try again or use \"Forgot Password.\"");
        });
    }

    @Test
    @Story("Авторизация запросов")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Запрос профиля без auth-заголовков отклоняется с 401")
    void requestWithoutAuthHeadersFails() {
        ErrorResponse error = UserApi.getUserWithoutAuth(401);

        step("Проверить ответ об ошибке", () -> {
            assertThat(error.getError()).isEqualTo("NotAuthorized");
            assertThat(error.getMessage()).isEqualTo("Missing authentication headers.");
        });
    }
}
