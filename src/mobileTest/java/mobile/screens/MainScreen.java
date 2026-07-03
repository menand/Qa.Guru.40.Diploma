package mobile.screens;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import java.time.Duration;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

/**
 * Главный экран приложения после логина: тулбар и нижняя навигация по типам задач.
 */
public class MainScreen {

    private final SelenideElement bottomNavigation = $(MobileBy.id("bottom_navigation"));
    private final SelenideElement todosTab = $(MobileBy.id("todos_tab"));

    @Step("Проверить, что открыт главный экран с задачами")
    public MainScreen checkOpened() {
        // после логина приложение синхронизирует данные — даём запас
        bottomNavigation.shouldBe(visible, Duration.ofSeconds(60));
        return this;
    }

    @Step("Открыть вкладку To Do's в нижней навигации")
    public MainScreen openTodosTab() {
        todosTab.click();
        return this;
    }

    @Step("Проверить, что задача «{text}» отображается в списке")
    public MainScreen checkTaskVisible(String text) {
        $(MobileBy.exactText(text)).shouldBe(visible, Duration.ofSeconds(20));
        return this;
    }
}
