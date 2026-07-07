package api.steps;

import api.models.ErrorResponse;
import api.models.RegisterRequest;
import api.models.UserCredentials;
import api.specs.ApiSpecs;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;

public final class AuthApi {

    private AuthApi() {
    }

    @Step("API: зарегистрировать пользователя {request.username}")
    public static UserCredentials register(RegisterRequest request) {
        Response response = given(ApiSpecs.anonSpec())
                .body(request)
                .post("/user/auth/local/register")
                .then()
                .spec(ApiSpecs.status(201))
                .extract().response();
        return UserCredentials.builder()
                .id(extractUserId(response))
                .apiToken(response.path("data.apiToken"))
                .username(request.getUsername())
                .password(request.getPassword())
                .build();
    }

    @Step("API: попытаться зарегистрировать {request.username}, ожидая ошибку {expectedStatus}")
    public static ErrorResponse registerExpectingError(RegisterRequest request, int expectedStatus) {
        return given(ApiSpecs.anonSpec())
                .body(request)
                .post("/user/auth/local/register")
                .then()
                .spec(ApiSpecs.status(expectedStatus))
                .extract().as(ErrorResponse.class);
    }

    @Step("API: логин пользователя {username}")
    public static UserCredentials login(String username, String password) {
        Response response = given(ApiSpecs.anonSpec())
                .body(Map.of("username", username, "password", password))
                .post("/user/auth/local/login")
                .then()
                .spec(ApiSpecs.status(200))
                .extract().response();
        return UserCredentials.builder()
                .id(extractUserId(response))
                .apiToken(response.path("data.apiToken"))
                .username(username)
                .password(password)
                .build();
    }

    @Step("API: попытаться войти как {username}, ожидая ошибку {expectedStatus}")
    public static ErrorResponse loginExpectingError(String username, String password, int expectedStatus) {
        return given(ApiSpecs.anonSpec())
                .body(Map.of("username", username, "password", password))
                .post("/user/auth/local/login")
                .then()
                .spec(ApiSpecs.status(expectedStatus))
                .extract().as(ErrorResponse.class);
    }

    // без @Step: вызывается в том числе из shutdown hook, где нет активного Allure-теста
    public static void deleteUser(UserCredentials user) {
        given(ApiSpecs.quietAuthSpec(user))
                .body(Map.of("password", user.getPassword()))
                .delete("/user")
                .then()
                .spec(ApiSpecs.status(200));
    }

    /**
     * Очистка в finally/shutdown hook: сбой удаления не должен маскировать
     * настоящую причину падения теста (исключение из finally заменяет исходное).
     */
    public static void deleteUserQuietly(UserCredentials user) {
        try {
            deleteUser(user);
        } catch (RuntimeException | AssertionError e) {
            System.err.println("Не удалось удалить тестовый аккаунт "
                    + user.getUsername() + ": " + e.getMessage());
        }
    }

    private static String extractUserId(Response response) {
        String id = response.path("data.id");
        return id != null ? id : response.path("data._id");
    }
}
