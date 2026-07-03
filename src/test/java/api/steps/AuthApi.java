package api.steps;

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
        Response response = registerRaw(request)
                .then()
                .statusCode(201)
                .extract().response();
        return UserCredentials.builder()
                .id(extractUserId(response))
                .apiToken(response.path("data.apiToken"))
                .username(request.getUsername())
                .email(request.getEmail())
                .password(request.getPassword())
                .build();
    }

    @Step("API: отправить запрос регистрации {request.username}")
    public static Response registerRaw(RegisterRequest request) {
        return given(ApiSpecs.anonSpec())
                .body(request)
                .post("/user/auth/local/register");
    }

    @Step("API: логин пользователя {username}")
    public static Response login(String username, String password) {
        return given(ApiSpecs.anonSpec())
                .body(Map.of("username", username, "password", password))
                .post("/user/auth/local/login");
    }

    @Step("API: удалить аккаунт пользователя {user.username}")
    public static void deleteUser(UserCredentials user) {
        given(ApiSpecs.quietAuthSpec(user))
                .body(Map.of("password", user.getPassword()))
                .delete("/user")
                .then()
                .statusCode(200);
    }

    private static String extractUserId(Response response) {
        String id = response.path("data.id");
        return id != null ? id : response.path("data._id");
    }
}
