package api.steps;

import api.models.ErrorResponse;
import api.models.HabiticaTask;
import api.models.ScoreDirection;
import api.models.ScoreResult;
import api.models.TaskType;
import api.models.UserCredentials;
import api.specs.ApiSpecs;
import io.qameta.allure.Step;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public final class TasksApi {

    private TasksApi() {
    }

    @Step("API: создать задачу типа {type} с текстом «{text}»")
    public static HabiticaTask createTask(UserCredentials user, String text, TaskType type) {
        return given(ApiSpecs.authSpec(user))
                .body(Map.of("text", text, "type", type.key()))
                .post("/tasks/user")
                .then()
                .spec(ApiSpecs.status(201))
                .extract().jsonPath().getObject("data", HabiticaTask.class);
    }

    @Step("API: получить список задач пользователя")
    public static List<HabiticaTask> getUserTasks(UserCredentials user) {
        return given(ApiSpecs.authSpec(user))
                .get("/tasks/user")
                .then()
                .spec(ApiSpecs.status(200))
                .extract().jsonPath().getList("data", HabiticaTask.class);
    }

    @Step("API: запросить задачу {taskId}, ожидая ошибку {expectedStatus}")
    public static ErrorResponse getTaskExpectingError(UserCredentials user, String taskId, int expectedStatus) {
        return given(ApiSpecs.authSpec(user))
                .get("/tasks/{taskId}", taskId)
                .then()
                .spec(ApiSpecs.status(expectedStatus))
                .extract().as(ErrorResponse.class);
    }

    @Step("API: изменить текст задачи {taskId} на «{text}»")
    public static HabiticaTask updateTaskText(UserCredentials user, String taskId, String text) {
        return given(ApiSpecs.authSpec(user))
                .body(Map.of("text", text))
                .put("/tasks/{taskId}", taskId)
                .then()
                .spec(ApiSpecs.status(200))
                .extract().jsonPath().getObject("data", HabiticaTask.class);
    }

    @Step("API: засчитать задачу {taskId} в направлении {direction}")
    public static ScoreResult scoreTask(UserCredentials user, String taskId, ScoreDirection direction) {
        return given(ApiSpecs.authSpec(user))
                .post("/tasks/{taskId}/score/{direction}", taskId, direction.key())
                .then()
                .spec(ApiSpecs.status(200))
                .extract().jsonPath().getObject("data", ScoreResult.class);
    }

    @Step("API: удалить задачу {taskId}")
    public static void deleteTask(UserCredentials user, String taskId) {
        given(ApiSpecs.authSpec(user))
                .delete("/tasks/{taskId}", taskId)
                .then()
                .spec(ApiSpecs.status(200));
    }
}
