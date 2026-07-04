package web.pages;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import java.time.Duration;

import static com.codeborne.selenide.Condition.attributeMatching;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.open;

/**
 * Лендинг https://habitica.com/static/home с формой быстрой регистрации.
 */
public class HomePage {

    private final SelenideElement header = $("h1");
    private final SelenideElement loginButton = $("a.login-button");
    private final SelenideElement getStartedLink = $("a.nav-link[href='/register']");
    private final SelenideElement emailInput = $("input[placeholder='Email']");
    private final SelenideElement passwordInput = $("input[placeholder='Password']");
    private final SelenideElement confirmPasswordInput = $("input[placeholder='Confirm Password']");
    private final SelenideElement continueButton = $("#continue-button");
    private final SelenideElement googlePlayLink = $("a[href*='play.google.com']");
    private final SelenideElement appStoreLink = $("a[href*='itunes.apple.com']");
    // после логаута попадаем либо на лендинг (кнопка Log In), либо на форму логина
    private final SelenideElement unauthorizedZoneMarker = $("a.login-button, #usernameInput");

    @Step("Открыть главную страницу /static/home")
    public HomePage openPage() {
        open("/static/home");
        header.shouldBe(visible);
        acceptCookiesIfShown();
        return this;
    }

    @Step("Принять cookies, если показан баннер")
    public static void acceptCookiesIfShown() {
        // баннер рендерится Vue с задержкой — ждём его немного и идём дальше, если не появился
        SelenideElement accept = $$("button").findBy(text("Accept All Cookies"));
        if (accept.is(visible, Duration.ofSeconds(4))) {
            accept.click();
        }
    }

    @Step("Проверить заголовок лендинга: «{expected}»")
    public HomePage checkHeader(String expected) {
        header.shouldHave(text(expected));
        return this;
    }

    @Step("Проверить, что форма быстрой регистрации отображается")
    public HomePage checkSignupFormVisible() {
        emailInput.shouldBe(visible);
        passwordInput.shouldBe(visible);
        confirmPasswordInput.shouldBe(visible);
        continueButton.shouldBe(visible);
        return this;
    }

    @Step("Проверить, что секция лендинга «{heading}» отображается")
    public HomePage checkSectionVisible(String heading) {
        $$("h2").findBy(text(heading)).shouldBe(visible);
        return this;
    }

    @Step("Проверить ссылки на мобильные приложения в сторах")
    public HomePage checkMobileStoreLinks() {
        googlePlayLink.shouldHave(
                attributeMatching("href", ".*play\\.google\\.com.*com\\.habitrpg\\.android\\.habitica.*"));
        appStoreLink.shouldHave(
                attributeMatching("href", ".*itunes\\.apple\\.com.*"));
        return this;
    }

    @Step("Перейти на страницу логина")
    public void goToLogin() {
        loginButton.click();
    }

    @Step("Перейти к регистрации по ссылке Get Started")
    public void goToRegister() {
        getStartedLink.click();
    }

    @Step("Проверить, что пользователь в неавторизованной зоне")
    public void checkLoggedOut() {
        unauthorizedZoneMarker.shouldBe(visible);
    }
}
