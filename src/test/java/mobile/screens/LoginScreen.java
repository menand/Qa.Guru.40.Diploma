package mobile.screens;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.appium.java_client.AppiumBy;
import io.qameta.allure.Step;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThanOrEqual;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

/**
 * Экран входа (Compose): сначала выбор способа входа,
 * по кнопке Login раскрывается форма username/password.
 */
public class LoginScreen {

    private final SelenideElement continueWithEmailButton = $(MobileBy.exactText("Continue with email"));
    // текст «Login» встречается на экране только один раз: на выборе способа входа это
    // кнопка-переключатель, в открытой форме — кнопка отправки
    private final SelenideElement openLoginFormButton = $(MobileBy.exactText("Login"));
    private final SelenideElement submitButton = $(MobileBy.exactText("Login"));
    private final SelenideElement forgotPasswordButton = $(MobileBy.exactText("Forgot Password"));
    // Compose-поля без id: username — первый EditText, password — второй
    private final ElementsCollection inputFields = $$(AppiumBy.className("android.widget.EditText"));

    @Step("Проверить, что показан выбор способа входа")
    public LoginScreen checkAuthOptionsVisible() {
        continueWithEmailButton.shouldBe(visible);
        openLoginFormButton.shouldBe(visible);
        return this;
    }

    @Step("Открыть форму логина")
    public LoginScreen openLoginForm() {
        openLoginFormButton.click();
        return this;
    }

    @Step("Проверить, что форма логина отображается")
    public LoginScreen checkLoginFormVisible() {
        inputFields.shouldHave(sizeGreaterThanOrEqual(2));
        forgotPasswordButton.shouldBe(visible);
        return this;
    }

    @Step("Войти под пользователем {username}")
    public MainScreen login(String username, String password) {
        inputFields.get(0).setValue(username);
        inputFields.get(1).setValue(password);
        submitButton.click();
        return new MainScreen();
    }
}
