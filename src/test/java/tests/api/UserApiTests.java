package tests.api;

import api.steps.UserApi;
import io.qameta.allure.Feature;
import io.qameta.allure.Link;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;

@Feature("Профиль пользователя")
@Link(name = "Habitica API doc", url = "https://habitica.com/apidoc/")
public class UserApiTests extends ApiTestBase {

    @Test
    @Story("Просмотр профиля")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("GET /user возвращает профиль с корректным username и стартовыми статами")
    void getUserReturnsProfile() {
        Response response = UserApi.getUser(USER);

        step("Проверить данные профиля", () -> {
            assertThat(response.statusCode()).isEqualTo(200);
            assertThat(response.<String>path("data.auth.local.username")).isEqualTo(USER.getUsername());
            assertThat(response.jsonPath().getInt("data.stats.lvl")).isGreaterThanOrEqualTo(1);
            assertThat(response.jsonPath().getDouble("data.stats.hp")).isPositive();
        });
    }

    @Test
    @Story("Редактирование профиля")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("PUT /user меняет отображаемое имя пользователя")
    void updateDisplayNameChangesProfileName() {
        String newName = "Дипломант " + USER.getUsername().substring(3, 9);

        Response response = UserApi.updateDisplayName(USER, newName);

        step("Проверить, что имя обновилось", () -> {
            assertThat(response.statusCode()).isEqualTo(200);
            assertThat(response.<String>path("data.profile.name")).isEqualTo(newName);
        });
    }
}
