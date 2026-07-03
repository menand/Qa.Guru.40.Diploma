package api.steps;

import api.models.ErrorResponse;
import api.models.UserCredentials;
import api.models.UserProfile;
import api.specs.ApiSpecs;
import io.qameta.allure.Step;

import java.util.Map;

import static io.restassured.RestAssured.given;

public final class UserApi {

    private UserApi() {
    }

    @Step("API: получить профиль пользователя")
    public static UserProfile getUser(UserCredentials user) {
        return given(ApiSpecs.authSpec(user))
                .get("/user")
                .then()
                .spec(ApiSpecs.status(200))
                .extract().jsonPath().getObject("data", UserProfile.class);
    }

    @Step("API: запросить профиль без авторизации, ожидая ошибку {expectedStatus}")
    public static ErrorResponse getUserWithoutAuth(int expectedStatus) {
        return given(ApiSpecs.anonSpec())
                .get("/user")
                .then()
                .spec(ApiSpecs.status(expectedStatus))
                .extract().as(ErrorResponse.class);
    }

    @Step("API: сменить отображаемое имя на «{displayName}»")
    public static UserProfile updateDisplayName(UserCredentials user, String displayName) {
        return given(ApiSpecs.authSpec(user))
                .body(Map.of("profile.name", displayName))
                .put("/user")
                .then()
                .spec(ApiSpecs.status(200))
                .extract().jsonPath().getObject("data", UserProfile.class);
    }

    @Step("API: отключить приветственный онбординг для пользователя")
    public static void markWelcomed(UserCredentials user) {
        given(ApiSpecs.quietAuthSpec(user))
                .body(Map.of("flags.welcomed", true))
                .put("/user")
                .then()
                .spec(ApiSpecs.status(200));
    }
}
