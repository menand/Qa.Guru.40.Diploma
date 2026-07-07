package tests.api;

import api.ApiErrors;
import api.models.ErrorResponse;
import api.models.HabiticaTask;
import api.models.ScoreDirection;
import api.models.ScoreResult;
import api.models.TaskType;
import api.models.UserProfile;
import api.steps.TasksApi;
import api.steps.UserApi;
import helpers.TestData;
import io.qameta.allure.Feature;
import io.qameta.allure.Link;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;

@Feature("Задачи")
@Link(name = "Habitica API doc", url = "https://habitica.com/apidoc/")
public class TasksApiTests extends ApiTestBase {

    @Test
    @Story("Создание задач")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Создание todo возвращает задачу с id, типом и исходным текстом")
    void createTodoReturnsTask() {
        String text = TestData.randomTaskTitle();

        HabiticaTask task = TasksApi.createTask(USER, text, TaskType.TODO);

        step("Проверить созданную задачу", () -> {
            assertThat(task.getId()).matches(UUID_REGEX);
            assertThat(task.getType()).isEqualTo(TaskType.TODO);
            assertThat(task.getText()).isEqualTo(text);
            assertThat(task.getCompleted()).isFalse();
        });
        step("Проверить дефолты новой задачи: пустые заметки, сложность easy, ценность 0", () -> {
            assertThat(task.getNotes()).isEmpty();
            assertThat(task.getPriority()).isEqualTo(1.0);
            assertThat(task.getValue()).isEqualTo(0.0);
        });
    }

    @Test
    @Story("Создание задач")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Создание habit возвращает задачу типа habit")
    void createHabitReturnsTask() {
        String text = TestData.randomTaskTitle();

        HabiticaTask task = TasksApi.createTask(USER, text, TaskType.HABIT);

        step("Проверить созданную привычку", () -> {
            assertThat(task.getId()).matches(UUID_REGEX);
            assertThat(task.getType()).isEqualTo(TaskType.HABIT);
            assertThat(task.getText()).isEqualTo(text);
        });
        step("Проверить дефолты привычки: активны обе кнопки «+» и «−»", () -> {
            assertThat(task.getUp()).isTrue();
            assertThat(task.getDown()).isTrue();
            assertThat(task.getValue()).isEqualTo(0.0);
        });
    }

    @Test
    @Story("Список задач")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("GET /tasks/user содержит созданную задачу")
    void taskListContainsCreatedTask() {
        HabiticaTask created = TasksApi.createTask(USER, TestData.randomTaskTitle(), TaskType.TODO);

        List<HabiticaTask> tasks = TasksApi.getUserTasks(USER);

        step("Проверить, что задача в списке ровно одна и не искажена", () ->
                assertThat(tasks)
                        .filteredOn(task -> created.getId().equals(task.getId()))
                        .singleElement()
                        .satisfies(task -> {
                            assertThat(task.getText()).isEqualTo(created.getText());
                            assertThat(task.getType()).isEqualTo(TaskType.TODO);
                        }));
    }

    @Test
    @Story("Редактирование задач")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("PUT /tasks/:id меняет текст задачи")
    void updateTaskChangesText() {
        HabiticaTask created = TasksApi.createTask(USER, TestData.randomTaskTitle(), TaskType.TODO);
        String newText = TestData.randomTaskTitle();

        HabiticaTask updated = TasksApi.updateTaskText(USER, created.getId(), newText);

        step("Проверить обновлённый текст; id и тип не изменились", () -> {
            assertThat(updated.getId()).isEqualTo(created.getId());
            assertThat(updated.getText()).isEqualTo(newText);
            assertThat(updated.getType()).isEqualTo(TaskType.TODO);
        });
    }

    @Test
    @Story("Выполнение задач")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Score todo вверх помечает задачу выполненной и начисляет опыт и золото")
    void scoreTodoUpGivesReward() {
        UserProfile.Stats before = UserApi.getUser(USER).getStats();
        HabiticaTask created = TasksApi.createTask(USER, TestData.randomTaskTitle(), TaskType.TODO);

        ScoreResult result = TasksApi.scoreTask(USER, created.getId(), ScoreDirection.UP);

        step("Проверить награду: свежая todo даёт дельту ровно 1, +6 XP и +1 GP (крит может дать больше)", () -> {
            assertThat(result.getDelta()).isEqualTo(1.0);
            assertThat(result.getExp()).isGreaterThanOrEqualTo(before.getExp() + 6);
            assertThat(result.getGp()).isGreaterThanOrEqualTo(before.getGp() + 1);
            // сравниваем с состоянием до скоринга: общий пользователь накапливает опыт от других тестов
            assertThat(result.getLvl()).isEqualTo(before.getLvl());
            assertThat(result.getHp()).isEqualTo(before.getHp());
        });
        step("Проверить, что задача помечена выполненной, а её ценность выросла до 1", () -> {
            HabiticaTask scored = TasksApi.getTask(USER, created.getId());
            assertThat(scored.getCompleted()).isTrue();
            assertThat(scored.getValue()).isEqualTo(1.0);
        });
    }

    @Test
    @Story("Удаление задач")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Удалённая задача недоступна: повторный GET возвращает 404 NotFound")
    void deletedTaskIsNotFound() {
        HabiticaTask created = TasksApi.createTask(USER, TestData.randomTaskTitle(), TaskType.TODO);

        TasksApi.deleteTask(USER, created.getId());
        ErrorResponse error = TasksApi.getTaskExpectingError(USER, created.getId(), 404);

        step("Проверить 404 по удалённой задаче", () -> {
            assertThat(error.getSuccess()).isFalse();
            assertThat(error.getError()).isEqualTo(ApiErrors.NOT_FOUND);
            assertThat(error.getMessage()).isEqualTo(ApiErrors.TASK_NOT_FOUND);
        });
    }
}
