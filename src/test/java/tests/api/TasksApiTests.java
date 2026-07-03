package tests.api;

import api.models.ErrorResponse;
import api.models.HabiticaTask;
import api.models.ScoreResult;
import api.steps.TasksApi;
import io.qameta.allure.Feature;
import io.qameta.allure.Link;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import net.datafaker.Faker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;

@Feature("Задачи")
@Link(name = "Habitica API doc", url = "https://habitica.com/apidoc/")
public class TasksApiTests extends ApiTestBase {

    private static final Faker FAKER = new Faker();

    private String randomTaskText() {
        return "Дипломная задача: " + FAKER.book().title();
    }

    @Test
    @Story("Создание задач")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Создание todo возвращает задачу с id, типом и исходным текстом")
    void createTodoReturnsTask() {
        String text = randomTaskText();

        HabiticaTask task = TasksApi.createTask(USER, text, "todo");

        step("Проверить созданную задачу", () -> {
            assertThat(task.getId()).isNotBlank();
            assertThat(task.getType()).isEqualTo("todo");
            assertThat(task.getText()).isEqualTo(text);
            assertThat(task.getCompleted()).isFalse();
        });
    }

    @Test
    @Story("Создание задач")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Создание habit возвращает задачу типа habit")
    void createHabitReturnsTask() {
        String text = randomTaskText();

        HabiticaTask task = TasksApi.createTask(USER, text, "habit");

        step("Проверить созданную привычку", () -> {
            assertThat(task.getId()).isNotBlank();
            assertThat(task.getType()).isEqualTo("habit");
            assertThat(task.getText()).isEqualTo(text);
        });
    }

    @Test
    @Story("Список задач")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("GET /tasks/user содержит созданную задачу")
    void taskListContainsCreatedTask() {
        HabiticaTask created = TasksApi.createTask(USER, randomTaskText(), "todo");

        List<HabiticaTask> tasks = TasksApi.getUserTasks(USER);

        step("Проверить, что задача есть в списке", () ->
                assertThat(tasks)
                        .extracting(HabiticaTask::getId)
                        .contains(created.getId()));
    }

    @Test
    @Story("Редактирование задач")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("PUT /tasks/:id меняет текст задачи")
    void updateTaskChangesText() {
        HabiticaTask created = TasksApi.createTask(USER, randomTaskText(), "todo");
        String newText = randomTaskText();

        HabiticaTask updated = TasksApi.updateTaskText(USER, created.getId(), newText);

        step("Проверить обновлённый текст", () -> {
            assertThat(updated.getId()).isEqualTo(created.getId());
            assertThat(updated.getText()).isEqualTo(newText);
        });
    }

    @Test
    @Story("Выполнение задач")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Score todo вверх засчитывает выполнение и даёт награду (delta > 0)")
    void scoreTodoUpGivesReward() {
        HabiticaTask created = TasksApi.createTask(USER, randomTaskText(), "todo");

        ScoreResult result = TasksApi.scoreTask(USER, created.getId(), "up");

        step("Проверить награду за выполнение", () -> {
            assertThat(result.getDelta()).isPositive();
            assertThat(result.getGp()).isNotNull();
            assertThat(result.getExp()).isNotNull();
        });
    }

    @Test
    @Story("Удаление задач")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Удалённая задача недоступна: повторный GET возвращает 404 NotFound")
    void deletedTaskIsNotFound() {
        HabiticaTask created = TasksApi.createTask(USER, randomTaskText(), "todo");

        TasksApi.deleteTask(USER, created.getId());
        Response getDeleted = TasksApi.getTaskRaw(USER, created.getId());

        step("Проверить 404 по удалённой задаче", () -> {
            assertThat(getDeleted.statusCode()).isEqualTo(404);
            ErrorResponse error = getDeleted.as(ErrorResponse.class);
            assertThat(error.getError()).isEqualTo("NotFound");
        });
    }
}
