package tests.web;

import api.models.UserCredentials;
import api.steps.UserApi;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.logevents.SelenideLogger;
import config.Configs;
import helpers.BrowserSession;
import helpers.TestUsers;
import io.qameta.allure.Epic;
import io.qameta.allure.Owner;
import io.qameta.allure.Step;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.openqa.selenium.chrome.ChromeOptions;
import web.pages.HomePage;
import web.pages.TasksPage;

import java.util.Map;

@Tag("web")
@Owner("menand")
@Epic("Habitica Web UI")
public abstract class WebTestBase {

    private static UserCredentials webUser;

    @BeforeAll
    static void configureSelenide() {
        Configuration.baseUrl = Configs.WEB.baseUrl();
        Configuration.browser = Configs.WEB.browser();
        Configuration.browserSize = Configs.WEB.browserSize();
        Configuration.timeout = Configs.WEB.timeout();
        Configuration.headless = Configs.WEB.headless();
        // SPA грузит тяжёлый лендинг: ждём только DOMContentLoaded
        Configuration.pageLoadStrategy = "eager";
        // тексты и плейсхолдеры в тестах английские — фиксируем локаль браузера
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--lang=en-US", "--no-sandbox", "--disable-dev-shm-usage", "--disable-gpu");
        options.setExperimentalOption("prefs", Map.of("intl.accept_languages", "en-US"));
        Configuration.browserCapabilities = options;
        SelenideLogger.addListener("AllureSelenide",
                new AllureSelenide().screenshots(true).savePageSource(true));
    }

    /** Общий пользователь для авторизованных web-тестов (без приветственного онбординга). */
    protected static synchronized UserCredentials webUser() {
        if (webUser == null) {
            webUser = TestUsers.shared();
            UserApi.markWelcomed(webUser);
        }
        return webUser;
    }

    @Step("Открыть приложение под пользователем (авторизация через localStorage)")
    protected TasksPage openAppAsUser(UserCredentials user) {
        BrowserSession.authorize(user);
        TasksPage tasksPage = new TasksPage().waitLoaded();
        HomePage.acceptCookiesIfShown();
        return tasksPage;
    }

    @AfterEach
    void tearDownBrowser() {
        // скриншот и page source к упавшим тестам прикладывает AllureSelenide-листенер
        if (WebDriverRunner.hasWebDriverStarted()) {
            Selenide.closeWebDriver();
        }
    }
}
