package api.steps;

import api.models.TagModel;
import api.models.UserCredentials;
import api.specs.ApiSpecs;
import io.qameta.allure.Step;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public final class TagsApi {

    private TagsApi() {
    }

    @Step("API: создать тег «{name}»")
    public static TagModel createTag(UserCredentials user, String name) {
        return given(ApiSpecs.authSpec(user))
                .body(Map.of("name", name))
                .post("/tags")
                .then()
                .spec(ApiSpecs.status(201))
                .extract().jsonPath().getObject("data", TagModel.class);
    }

    @Step("API: получить список тегов")
    public static List<TagModel> getTags(UserCredentials user) {
        return given(ApiSpecs.authSpec(user))
                .get("/tags")
                .then()
                .spec(ApiSpecs.status(200))
                .extract().jsonPath().getList("data", TagModel.class);
    }

    @Step("API: переименовать тег {tagId} в «{name}»")
    public static TagModel updateTag(UserCredentials user, String tagId, String name) {
        return given(ApiSpecs.authSpec(user))
                .body(Map.of("name", name))
                .put("/tags/{tagId}", tagId)
                .then()
                .spec(ApiSpecs.status(200))
                .extract().jsonPath().getObject("data", TagModel.class);
    }

    @Step("API: удалить тег {tagId}")
    public static void deleteTag(UserCredentials user, String tagId) {
        given(ApiSpecs.authSpec(user))
                .delete("/tags/{tagId}", tagId)
                .then()
                .spec(ApiSpecs.status(200));
    }
}
