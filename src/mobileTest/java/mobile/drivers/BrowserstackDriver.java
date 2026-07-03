package mobile.drivers;

import com.codeborne.selenide.WebDriverProvider;
import config.Configs;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.Map;

/**
 * Провайдер Appium-драйвера для BrowserStack App Automate.
 * Приложение должно быть заранее загружено в BrowserStack (см. ключ app в browserstack.properties).
 */
public class BrowserstackDriver implements WebDriverProvider {

    @Override
    public WebDriver createDriver(Capabilities capabilities) {
        MutableCapabilities caps = new MutableCapabilities();
        caps.setCapability("platformName", "android");
        caps.setCapability("appium:automationName", "UiAutomator2");
        caps.setCapability("appium:app", Configs.BROWSERSTACK.app());
        caps.setCapability("appium:deviceName", Configs.BROWSERSTACK.device());
        caps.setCapability("appium:platformVersion", Configs.BROWSERSTACK.osVersion());
        caps.setCapability("bstack:options", Map.of(
                "userName", Configs.BROWSERSTACK.user(),
                "accessKey", Configs.BROWSERSTACK.key(),
                "projectName", Configs.BROWSERSTACK.project(),
                "buildName", Configs.BROWSERSTACK.build()
        ));
        try {
            return new AndroidDriver(URI.create(Configs.BROWSERSTACK.remoteUrl()).toURL(), caps);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Некорректный remoteUrl BrowserStack: "
                    + Configs.BROWSERSTACK.remoteUrl(), e);
        }
    }
}
