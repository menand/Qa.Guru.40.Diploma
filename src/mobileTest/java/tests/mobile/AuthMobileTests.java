package tests.mobile;

import api.models.TaskType;
import api.models.UserCredentials;
import api.steps.TasksApi;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import mobile.screens.IntroScreen;
import net.datafaker.Faker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Feature("Аутентификация в мобильном приложении")
public class AuthMobileTests extends MobileTestBase {

    @Test
    @Story("Логин")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Логин с валидными кредами открывает главный экран с задачами")
    void loginWithValidCredentialsOpensTasks() {
        UserCredentials user = mobileUser();

        new IntroScreen().checkOpened()
                .skip()
                .openLoginForm()
                .checkLoginFormVisible()
                .login(user.getUsername(), user.getPassword())
                .checkOpened();
    }

    @Test
    @Story("Навигация")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Созданная через API todo отображается на вкладке To Do's")
    void createdTodoIsShownOnTodosTab() {
        UserCredentials user = mobileUser();
        String text = "task-" + new Faker().regexify("[a-z0-9]{8}");
        TasksApi.createTask(user, text, TaskType.TODO);

        new IntroScreen().checkOpened()
                .skip()
                .openLoginForm()
                .login(user.getUsername(), user.getPassword())
                .checkOpened()
                .openTodosTab()
                .checkTaskVisible(text);
    }
}
