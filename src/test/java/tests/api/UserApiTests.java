package tests.api;

import api.models.UserProfile;
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

@Feature("Профиль пользователя")
@Link(name = "Habitica API doc", url = "https://habitica.com/apidoc/")
public class UserApiTests extends ApiTestBase {

    @Test
    @Story("Просмотр профиля")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("GET /user возвращает профиль с корректным username и стартовыми статами")
    void getUserReturnsProfile() {
        UserProfile profile = UserApi.getUser(USER);

        step("Проверить данные профиля", () -> {
            assertThat(profile.getId()).isEqualTo(USER.getId());
            assertThat(profile.getAuth().getLocal().getUsername()).isEqualTo(USER.getUsername());
            assertThat(profile.getAuth().getLocal().getEmail())
                    .isEqualTo(USER.getUsername() + TestUsers.EMAIL_DOMAIN);
        });
        step("Проверить стартовые статы свежего аккаунта", () -> {
            assertThat(profile.getStats().getLvl()).isEqualTo(1);
            assertThat(profile.getStats().getHp()).isEqualTo(50.0);
        });
    }

    @Test
    @Story("Редактирование профиля")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("PUT /user меняет отображаемое имя пользователя")
    void updateDisplayNameChangesProfileName() {
        String newName = "Дипломант " + USER.getUsername().substring(3, 9);

        UserProfile updated = UserApi.updateDisplayName(USER, newName);

        step("Проверить, что имя обновилось, а логин не изменился", () -> {
            assertThat(updated.getProfile().getName()).isEqualTo(newName);
            assertThat(updated.getAuth().getLocal().getUsername()).isEqualTo(USER.getUsername());
        });
    }
}
