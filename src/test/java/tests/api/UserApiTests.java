package tests.api;

import api.models.UserCredentials;
import api.models.UserProfile;
import api.steps.AuthApi;
import api.steps.UserApi;
import helpers.TestData;
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
    }

    @Test
    @Story("Просмотр профиля")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Свежий аккаунт создаётся со стартовыми статами: уровень 1, 50 HP, 0 опыта и золота")
    void freshAccountHasStartStats() {
        // общий USER мутируется другими тестами (скоринг даёт опыт) — статы проверяем на свежем аккаунте
        UserCredentials fresh = AuthApi.register(TestUsers.randomRegisterRequest());

        try {
            UserProfile profile = UserApi.getUser(fresh);

            step("Проверить стартовые статы", () -> {
                assertThat(profile.getStats().getLvl()).isEqualTo(1);
                assertThat(profile.getStats().getHp()).isEqualTo(50.0);
                assertThat(profile.getStats().getExp()).isEqualTo(0.0);
                assertThat(profile.getStats().getGp()).isEqualTo(0.0);
            });
        } finally {
            step("Очистка: удалить свежий аккаунт", () -> AuthApi.deleteUserQuietly(fresh));
        }
    }

    @Test
    @Story("Редактирование профиля")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("PUT /user меняет отображаемое имя пользователя")
    void updateDisplayNameChangesProfileName() {
        String newName = TestData.randomDisplayName();

        UserProfile updated = UserApi.updateDisplayName(USER, newName);

        step("Проверить, что имя обновилось, а логин не изменился", () -> {
            assertThat(updated.getProfile().getName()).isEqualTo(newName);
            assertThat(updated.getAuth().getLocal().getUsername()).isEqualTo(USER.getUsername());
        });
    }
}
