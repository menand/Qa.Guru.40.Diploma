package tests.web;

import io.qameta.allure.Feature;
import io.qameta.allure.Link;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import web.pages.HomePage;
import web.pages.LoginPage;
import web.pages.RegisterPage;

import static com.codeborne.selenide.Selenide.webdriver;
import static com.codeborne.selenide.WebDriverConditions.urlContaining;

@Feature("Главная страница")
@Link(name = "Habitica home", url = "https://habitica.com/static/home")
public class HomePageTests extends WebTestBase {

    private final HomePage homePage = new HomePage();
    private final LoginPage loginPage = new LoginPage();
    private final RegisterPage registerPage = new RegisterPage();

    @Test
    @Story("Контент лендинга")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("На главной отображаются заголовок и форма быстрой регистрации")
    void mainPageShowsHeaderAndSignupForm() {
        homePage.openPage()
                .checkHeader("Motivate yourself to achieve your goals")
                .checkSignupFormVisible();
    }

    @Test
    @Story("Навигация")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Кнопка Log In ведёт на страницу логина с формой входа")
    void loginButtonLeadsToLoginForm() {
        homePage.openPage().goToLogin();

        webdriver().shouldHave(urlContaining("/login"));
        loginPage.checkLoginFormVisible();
    }

    @Test
    @Story("Навигация")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Ссылка Get Started ведёт на страницу регистрации")
    void getStartedLeadsToRegisterPage() {
        homePage.openPage().goToRegister();

        webdriver().shouldHave(urlContaining("/register"));
        registerPage.checkRegisterFormVisible();
    }

    @Test
    @Story("Контент лендинга")
    @Severity(SeverityLevel.MINOR)
    @DisplayName("Ссылки на мобильные приложения ведут в Google Play и App Store")
    void mobileAppLinksPointToStores() {
        homePage.openPage().checkMobileStoreLinks();
    }

    @ParameterizedTest(name = "На главной отображается секция «{0}»")
    @ValueSource(strings = {
            "Gamify Your Life",
            "Players Use Habitica to Improve",
            "Level Up Anywhere"
    })
    @Story("Контент лендинга")
    @Severity(SeverityLevel.MINOR)
    @DisplayName("Секции лендинга отображаются")
    void landingSectionIsDisplayed(String heading) {
        homePage.openPage().checkSectionVisible(heading);
    }
}
