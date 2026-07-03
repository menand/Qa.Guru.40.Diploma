package tests.mobile;

import api.models.UserCredentials;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import mobile.screens.IntroScreen;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Feature("Аутентификация в мобильном приложении")
public class AuthMobileTests extends MobileTestBase {

    @Test
    @Story("Логин")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Логин с валидными кредами открывает главный экран с задачами")
    void loginWithValidCredentialsOpensTasks() {
        UserCredentials user = mobileUser();

        new IntroScreen().checkOpened()
                .skip()
                .openLoginForm()
                .checkLoginFormVisible()
                .login(user.getUsername(), user.getPassword())
                .checkOpened();
    }

    @Test
    @Story("Навигация")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Нижняя навигация переключает на вкладку To Do's")
    void bottomNavigationSwitchesToTodos() {
        UserCredentials user = mobileUser();

        new IntroScreen().checkOpened()
                .skip()
                .openLoginForm()
                .login(user.getUsername(), user.getPassword())
                .checkOpened()
                .openTodosTab();
    }
}
