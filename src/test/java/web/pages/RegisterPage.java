package web.pages;

import com.codeborne.selenide.ClickOptions;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import java.time.Duration;

import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.selected;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.open;

/**
 * Двухшаговая регистрация: /register (email + пароль) -> /username (имя + согласие).
 */
public class RegisterPage {

    private final SelenideElement emailInput = $("#emailInput");
    private final SelenideElement passwordInput = $("#passwordInput");
    private final SelenideElement confirmPasswordInput = $("#confirmPasswordInput");
    private final SelenideElement continueButton = $("#continue-button");
    private final SelenideElement usernameInput = $("#usernameInput");
    private final SelenideElement tosCheckbox = $("#privacyTOS");

    @Step("Открыть страницу регистрации")
    public RegisterPage openPage() {
        open("/register");
        emailInput.shouldBe(visible);
        HomePage.acceptCookiesIfShown();
        return this;
    }

    @Step("Проверить, что форма регистрации отображается")
    public RegisterPage checkRegisterFormVisible() {
        emailInput.shouldBe(visible);
        continueButton.shouldBe(visible);
        return this;
    }

    @Step("Шаг 1: заполнить email {email} и пароль, нажать Continue")
    public RegisterPage submitCredentials(String email, String password) {
        emailInput.setValue(email).shouldHave(value(email));
        passwordInput.setValue(password).shouldHave(value(password));
        confirmPasswordInput.setValue(password).shouldHave(value(password));
        continueButton.shouldBe(enabled).click();
        return this;
    }

    @Step("Шаг 2: указать username {username}, принять условия и завершить регистрацию")
    public void submitUsername(String username) {
        usernameInput.shouldBe(visible).setValue(username);
        usernameInput.shouldHave(value(username));
        HomePage.acceptCookiesIfShown();
        tosCheckbox.click(ClickOptions.usingJavaScript());
        tosCheckbox.shouldBe(selected);
        // кнопка активируется после асинхронной проверки доступности username
        $$("button").findBy(text("Get Started"))
                .shouldBe(enabled, Duration.ofSeconds(30))
                .click();
    }
}
