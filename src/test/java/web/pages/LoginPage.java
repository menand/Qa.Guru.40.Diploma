package web.pages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

/**
 * Страница логина https://habitica.com/login.
 */
public class LoginPage {

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

    @Step("Проверить сообщение об ошибке логина: «{expectedText}»")
    public LoginPage checkErrorNotification(String expectedText) {
        errorNotification.shouldBe(visible).shouldHave(text(expectedText));
        return this;
    }
}
