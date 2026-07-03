package web.pages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.cssValue;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

/**
 * Страница логина https://habitica.com/login.
 */
public class LoginPage {

    /** Красный фон тоста об ошибке (маруновая палитра Habitica, #DE3F3F). */
    private static final String ERROR_BACKGROUND = "rgba(222, 63, 63, 1)";
    private static final String ERROR_TEXT_COLOR = "rgba(255, 255, 255, 1)";

    private final SelenideElement usernameInput = $("#usernameInput");
    private final SelenideElement passwordInput = $("#passwordInput");
    private final SelenideElement submitButton = $("form button[type='submit']");
    private final SelenideElement errorNotification = $(".notification.error");

    @Step("Открыть страницу логина")
    public LoginPage openPage() {
        open("/login");
        usernameInput.shouldBe(visible);
        HomePage.acceptCookiesIfShown();
        return this;
    }

    @Step("Войти под пользователем {username}")
    public LoginPage login(String username, String password) {
        usernameInput.setValue(username);
        passwordInput.setValue(password);
        submitButton.click();
        return this;
    }

    @Step("Проверить, что форма логина отображается")
    public LoginPage checkLoginFormVisible() {
        usernameInput.shouldBe(visible);
        passwordInput.shouldBe(visible);
        submitButton.shouldBe(visible);
        return this;
    }

    @Step("Проверить красный тост с сообщением: «{expectedText}»")
    public LoginPage checkErrorNotification(String expectedText) {
        errorNotification.shouldBe(visible)
                .shouldHave(text(expectedText))
                .shouldHave(cssValue("background-color", ERROR_BACKGROUND))
                .shouldHave(cssValue("color", ERROR_TEXT_COLOR));
        return this;
    }
}
