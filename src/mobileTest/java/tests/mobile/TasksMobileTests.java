package tests.mobile;

import api.models.TaskType;
import api.models.UserCredentials;
import api.steps.TasksApi;
import helpers.TestData;
import io.qameta.allure.Feature;
import io.qameta.allure.Link;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import mobile.screens.IntroScreen;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Feature("Задачи в мобильном приложении")
@Link(name = "Habitica 4.4 apk", url = "https://github.com/HabitRPG/habitica-android/releases/tag/4.4")
public class TasksMobileTests extends MobileTestBase {

    @Test
    @Story("Отображение задач")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Созданная через API todo отображается на вкладке To Do's")
    void createdTodoIsShownOnTodosTab() {
        UserCredentials user = mobileUser();
        String text = TestData.randomTaskText();
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
