package mobile.screens;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import java.time.Duration;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

/**
 * Стартовый экран авторизации: «начать новую игру» / «показать форму логина»,
 * по show_login_button раскрывается форма username/password.
 */
public class LoginScreen {

    private final SelenideElement newGameButton = $(MobileBy.id("new_game_button"));
    private final SelenideElement showLoginButton = $(MobileBy.id("show_login_button"));
    private final SelenideElement usernameInput = $(MobileBy.id("username"));
    private final SelenideElement passwordInput = $(MobileBy.id("password"));
    private final SelenideElement loginButton = $(MobileBy.id("login_btn"));
    private final SelenideElement forgotPasswordButton = $(MobileBy.id("forgot_password"));

    @Step("Проверить, что показан выбор способа входа")
    public LoginScreen checkAuthOptionsVisible() {
        newGameButton.shouldBe(visible, Duration.ofSeconds(20));
        showLoginButton.shouldBe(visible);
        return this;
    }

    @Step("Открыть форму логина")
    public LoginScreen openLoginForm() {
        showLoginButton.shouldBe(visible, Duration.ofSeconds(20)).click();
        return this;
    }

    @Step("Проверить, что форма логина отображается")
    public LoginScreen checkLoginFormVisible() {
        usernameInput.shouldBe(visible);
        passwordInput.shouldBe(visible);
        loginButton.shouldBe(visible);
        forgotPasswordButton.shouldBe(visible);
        return this;
    }

    @Step("Войти под пользователем {username}")
    public MainScreen login(String username, String password) {
        usernameInput.setValue(username);
        passwordInput.setValue(password);
        loginButton.click();
        return new MainScreen();
    }
}
