package api.steps;

import api.models.UserCredentials;
import api.specs.ApiSpecs;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;

public final class UserApi {

    private UserApi() {
    }

    @Step("API: получить профиль пользователя")
    public static Response getUser(UserCredentials user) {
        return given(ApiSpecs.authSpec(user))
                .get("/user");
    }

    @Step("API: сменить отображаемое имя на «{displayName}»")
    public static Response updateDisplayName(UserCredentials user, String displayName) {
        return given(ApiSpecs.authSpec(user))
                .body(Map.of("profile.name", displayName))
                .put("/user");
    }
}
