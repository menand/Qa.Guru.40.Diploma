package tests.web;

import api.models.RegisterRequest;
import api.models.UserCredentials;
import api.steps.AuthApi;
import api.steps.UserApi;
import helpers.BrowserSession;
import helpers.TestUsers;
import io.qameta.allure.Feature;
import io.qameta.allure.Link;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import web.pages.LoginPage;
import web.pages.RegisterPage;
import web.pages.TasksPage;

import static com.codeborne.selenide.Selenide.webdriver;
import static com.codeborne.selenide.WebDriverConditions.urlContaining;
import static io.qameta.allure.Allure.step;

@Feature("Аутентификация в вебе")
@Link(name = "Habitica home", url = "https://habitica.com/static/home")
public class AuthWebTests extends WebTestBase {

    @Test
    @Story("Регистрация")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Регистрация через UI (двухшаговая форма) приводит в приложение")
    void uiRegistrationCreatesNewUser() {
        RegisterRequest data = TestUsers.randomRegisterRequest();

        RegisterPage registerPage = new RegisterPage().openPage();
        registerPage.submitCredentials(data.getEmail(), data.getPassword());
        webdriver().shouldHave(urlContaining("/username"));
        registerPage.submitUsername(data.getUsername());

        new TasksPage().waitLoaded()
                .checkCharacterName(data.getUsername());

        step("Очистка: удалить созданного пользователя через API", () ->
                AuthApi.deleteUser(BrowserSession.currentUser(data.getUsername(), data.getPassword())));
    }

    @Test
    @Story("Логин")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Логин с валидными кредами открывает страницу задач с именем персонажа")
    void loginWithValidCredentialsOpensApp() {
        UserCredentials user = webUser();
        // имя в шапке — display name профиля; берём актуальное через API,
        // потому что API-тесты в объединённом прогоне могут его переименовать
        String displayName = UserApi.getUser(user).getProfile().getName();

        new LoginPage().openPage().login(user.getUsername(), user.getPassword());

        new TasksPage().waitLoaded().checkCharacterName(displayName);
    }

    @Test
    @Story("Логин")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Логин с неверным паролем показывает ошибку и оставляет на странице логина")
    void loginWithWrongPasswordShowsError() {
        UserCredentials user = webUser();

        new LoginPage().openPage()
                .login(user.getUsername(), "wrong-password-123")
                .checkErrorNotification("Your email, username, or password are incorrect. "
                        + "Please try again or use \"Forgot Password.\"");

        webdriver().shouldHave(urlContaining("/login"));
    }

    @Test
    @Story("Логаут")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Log Out завершает сессию и возвращает на неавторизованную зону")
    void logoutEndsSession() {
        openAppAsUser(webUser())
                .logout()
                .checkLoggedOut();
    }
}
