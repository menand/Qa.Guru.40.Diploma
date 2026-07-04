package tests.mobile;

import api.models.UserCredentials;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.appium.SelenideAppium;
import com.codeborne.selenide.logevents.SelenideLogger;
import helpers.Attachments;
import helpers.TestUsers;
import io.qameta.allure.Epic;
import io.qameta.allure.Owner;
import io.qameta.allure.selenide.AllureSelenide;
import mobile.drivers.BrowserstackDriver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.openqa.selenium.remote.RemoteWebDriver;

@Tag("mobile")
@Owner("menand")
@Epic("Habitica Mobile")
public abstract class MobileTestBase {

    /** Общий пользователь для мобильных тестов логина (без приветственного онбординга). */
    protected static UserCredentials mobileUser() {
        return TestUsers.sharedWelcomed();
    }

    @BeforeAll
    static void configureAppium() {
        Configuration.browser = BrowserstackDriver.class.getName();
        Configuration.browserSize = null;
        // CI передаёт -Dheadless=true всем слоям; для реального устройства флаг не только
        // бессмыслен — Selenide падает, применяя его к кастомному WebDriverProvider
        Configuration.headless = false;
        Configuration.timeout = 30_000;
        SelenideLogger.addListener("AllureSelenide",
                new AllureSelenide().screenshots(true).savePageSource(false));
    }

    @BeforeEach
    void launchApp() {
        SelenideAppium.launchApp();
    }

    @AfterEach
    void tearDownDriver() {
        if (!WebDriverRunner.hasWebDriverStarted()) {
            return;
        }
        String sessionId = ((RemoteWebDriver) WebDriverRunner.getWebDriver()).getSessionId().toString();
        Selenide.closeWebDriver();
        Attachments.browserstackVideo(sessionId);
    }
}
