package tests.api;

import api.models.ErrorResponse;
import api.models.RegisterRequest;
import api.models.UserCredentials;
import api.specs.ApiSpecs;
import api.steps.AuthApi;
import helpers.TestUsers;
import io.qameta.allure.Feature;
import io.qameta.allure.Link;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
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

        step("Проверить, что вернулись валидные учётные данные", () -> {
            assertThat(created.getId()).isNotBlank();
            assertThat(created.getApiToken()).isNotBlank();
        });
        step("Очистка: удалить созданный аккаунт", () -> AuthApi.deleteUser(created));
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

        Response response = AuthApi.registerRaw(request);

        step("Проверить ответ об ошибке", () -> {
            assertThat(response.statusCode()).isEqualTo(401);
            ErrorResponse error = response.as(ErrorResponse.class);
            assertThat(error.getSuccess()).isFalse();
            assertThat(error.getError()).isEqualTo("NotAuthorized");
            assertThat(error.getMessage()).isNotBlank();
        });
    }

    @Test
    @Story("Логин")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Логин с валидными кредами возвращает id и apiToken пользователя")
    void successfulLoginReturnsToken() {
        Response response = AuthApi.login(USER.getUsername(), USER.getPassword());

        step("Проверить учётные данные в ответе", () -> {
            assertThat(response.statusCode()).isEqualTo(200);
            assertThat(response.<String>path("data.id")).isEqualTo(USER.getId());
            assertThat(response.<String>path("data.apiToken")).isNotBlank();
        });
    }

    @Test
    @Story("Логин")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Логин с неверным паролем отклоняется с 401 NotAuthorized")
    void loginWithWrongPasswordFails() {
        Response response = AuthApi.login(USER.getUsername(), "wrong-password-123");

        step("Проверить ответ об ошибке", () -> {
            assertThat(response.statusCode()).isEqualTo(401);
            ErrorResponse error = response.as(ErrorResponse.class);
            assertThat(error.getError()).isEqualTo("NotAuthorized");
            assertThat(error.getMessage()).isNotBlank();
        });
    }

    @Test
    @Story("Авторизация запросов")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Запрос профиля без auth-заголовков отклоняется с 401")
    void requestWithoutAuthHeadersFails() {
        Response response = step("Запросить GET /user без x-api-user и x-api-key",
                () -> given(ApiSpecs.anonSpec()).get("/user"));

        step("Проверить ответ об ошибке", () -> {
            assertThat(response.statusCode()).isEqualTo(401);
            ErrorResponse error = response.as(ErrorResponse.class);
            assertThat(error.getError()).isEqualTo("NotAuthorized");
            assertThat(error.getMessage()).containsIgnoringCase("authentication");
        });
    }
}
