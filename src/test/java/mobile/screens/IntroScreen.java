package mobile.screens;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import java.time.Duration;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

/**
 * Интро-онбординг при первом запуске приложения (3 слайда, Compose).
 */
public class IntroScreen {

    private final SelenideElement skipButton = $(MobileBy.exactText("Skip"));
    private final SelenideElement nextButton = $(MobileBy.exactText("Next"));

    @Step("Проверить, что интро отображается")
    public IntroScreen checkOpened() {
        // холодный старт приложения на ферме устройств бывает долгим
        skipButton.shouldBe(visible, Duration.ofSeconds(60));
        nextButton.shouldBe(visible);
        return this;
    }

    @Step("Пропустить интро кнопкой Skip")
    public LoginScreen skip() {
        skipButton.click();
        return new LoginScreen();
    }
}
