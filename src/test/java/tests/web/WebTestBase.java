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
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
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
        if (!Configs.WEB.browserVersion().isBlank()) {
            Configuration.browserVersion = Configs.WEB.browserVersion();
        }
        // SPA грузит тяжёлый лендинг: ждём только DOMContentLoaded и даём запас на холодный старт
        Configuration.pageLoadStrategy = "eager";
        Configuration.pageLoadTimeout = 60_000;
        // page source при падении прикладывает AllureSelenide напрямую через WebDriver;
        // встроенный экстрактор Selenide нельзя: selenide-appium подменяет его своим,
        // который аугментирует драйвер CDP-соединением и падает на Selenoid по websocket
        Configuration.savePageSource = false;

        MutableCapabilities options = browserOptions(Configs.WEB.browser());
        if (!Configs.WEB.remoteUrl().isBlank()) {
            Configuration.remote = Configs.WEB.remoteUrl();
            // Selenoid: VNC для отладки, видео по флагу videoEnabled
            options.setCapability("selenoid:options",
                    Map.of("enableVNC", true, "enableVideo", Configs.WEB.videoEnabled()));
        }
        Configuration.browserCapabilities = options;
        SelenideLogger.addListener("AllureSelenide",
                new AllureSelenide().screenshots(true).savePageSource(true));
    }

    /** Опции под конкретный браузер; локаль en-US — тексты в тестах английские. */
    private static MutableCapabilities browserOptions(String browser) {
        switch (browser.toLowerCase()) {
            case "chrome": {
                ChromeOptions chrome = new ChromeOptions();
                chrome.addArguments("--lang=en-US", "--no-sandbox", "--disable-dev-shm-usage", "--disable-gpu");
                chrome.setExperimentalOption("prefs", Map.of("intl.accept_languages", "en-US"));
                return chrome;
            }
            case "firefox": {
                FirefoxOptions firefox = new FirefoxOptions();
                firefox.addPreference("intl.accept_languages", "en-US, en");
                return firefox;
            }
            default:
                return new MutableCapabilities();
        }
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
