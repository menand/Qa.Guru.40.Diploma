package tests.mobile;

import io.qameta.allure.Feature;
import io.qameta.allure.Link;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import mobile.screens.IntroScreen;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Feature("Онбординг в мобильном приложении")
@Link(name = "Habitica 4.4 apk", url = "https://github.com/HabitRPG/habitica-android/releases/tag/4.4")
public class OnboardingMobileTests extends MobileTestBase {

    @Test
    @Story("Интро")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("При первом запуске приложения показывается интро")
    void appLaunchShowsIntro() {
        new IntroScreen().checkOpened();
    }

    @Test
    @Story("Интро")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Skip пропускает интро и показывает выбор способа входа")
    void skipLeadsToAuthOptions() {
        new IntroScreen().checkOpened()
                .skip()
                .checkAuthOptionsVisible();
    }

    @Test
    @Story("Форма логина")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Кнопка Login открывает форму входа с полями и Forgot Password")
    void loginButtonOpensLoginForm() {
        new IntroScreen().checkOpened()
                .skip()
                .checkAuthOptionsVisible()
                .openLoginForm()
                .checkLoginFormVisible();
    }
}
