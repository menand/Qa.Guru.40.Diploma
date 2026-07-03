package tests.web;

import api.models.TaskType;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import web.pages.TasksPage;

@Feature("Управление задачами")
public class TasksWebTests extends WebTestBase {

    private static final Faker FAKER = new Faker();
    private TasksPage tasksPage;

    private String randomTaskText() {
        return "task-" + FAKER.regexify("[a-z0-9]{8}");
    }

    @BeforeEach
    void openApp() {
        tasksPage = openAppAsUser(webUser());
    }

    @Test
    @Story("Создание задач")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Todo, созданная через quick-add, появляется в колонке To Do's")
    void createTodoViaQuickAdd() {
        String text = randomTaskText();

        tasksPage.addTask(TaskType.TODO, text)
                .checkTaskVisible(TaskType.TODO, text);
    }

    @Test
    @Story("Создание задач")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Привычка, созданная через quick-add, появляется в колонке Habits")
    void createHabitViaQuickAdd() {
        String text = randomTaskText();

        tasksPage.addTask(TaskType.HABIT, text)
                .checkTaskVisible(TaskType.HABIT, text);
    }

    @Test
    @Story("Выполнение задач")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Выполненная todo пропадает из списка активных")
    void completedTodoLeavesActiveList() {
        String text = randomTaskText();
        tasksPage.addTask(TaskType.TODO, text)
                .checkTaskVisible(TaskType.TODO, text);

        tasksPage.completeTodo(text)
                .checkTaskHidden(TaskType.TODO, text);
    }

    @Test
    @Story("Редактирование задач")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Изменение заголовка задачи через модалку редактирования")
    void editTaskChangesTitle() {
        String text = randomTaskText();
        String newText = randomTaskText();
        tasksPage.addTask(TaskType.TODO, text);

        tasksPage.openTaskEdit(TaskType.TODO, text).changeTitleAndSave(newText);

        tasksPage.checkTaskVisible(TaskType.TODO, newText)
                .checkTaskAbsent(TaskType.TODO, text);
    }

    @Test
    @Story("Удаление задач")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Удалённая через меню карточки задача исчезает из колонки")
    void deletedTaskDisappears() {
        String text = randomTaskText();
        tasksPage.addTask(TaskType.TODO, text)
                .checkTaskVisible(TaskType.TODO, text);

        tasksPage.deleteTask(TaskType.TODO, text)
                .checkTaskAbsent(TaskType.TODO, text);
    }

    @Test
    @Story("Поиск задач")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Поиск фильтрует список задач по тексту")
    void searchFiltersTasks() {
        String matching = "alpha-" + FAKER.regexify("[a-z0-9]{6}");
        String other = "beta-" + FAKER.regexify("[a-z0-9]{6}");
        tasksPage.addTask(TaskType.TODO, matching)
                .addTask(TaskType.TODO, other)
                .checkTaskVisible(TaskType.TODO, other);

        tasksPage.search(matching)
                .checkTaskVisible(TaskType.TODO, matching)
                .checkTaskHidden(TaskType.TODO, other);
    }
}
