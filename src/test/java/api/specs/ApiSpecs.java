package api.specs;

import api.models.UserCredentials;
import config.Configs;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

public final class ApiSpecs {

    private ApiSpecs() {
    }

    /** Спека для неавторизованных запросов (регистрация, логин, негативные проверки). */
    public static RequestSpecification anonSpec() {
        return baseBuilder()
                .addFilter(new AllureRestAssured())
                .build();
    }

    /**
     * Спека авторизованного пользователя: заголовки x-api-user / x-api-key.
     * AllureRestAssured прикладывает запросы (включая auth-заголовки) в отчёт,
     * который публикуется на GitHub Pages. Осознанный компромисс: пользователь
     * одноразовый и удаляется в конце прогона — токен мёртв к моменту публикации.
     */
    public static RequestSpecification authSpec(UserCredentials user) {
        return baseBuilder()
                .addFilter(new AllureRestAssured())
                .addHeader("x-api-user", user.getId())
                .addHeader("x-api-key", user.getApiToken())
                .build();
    }

    /** Спека без Allure-вложений — для сервисных вызовов (подготовка данных, shutdown hook). */
    public static RequestSpecification quietAuthSpec(UserCredentials user) {
        return baseBuilder()
                .addHeader("x-api-user", user.getId())
                .addHeader("x-api-key", user.getApiToken())
                .build();
    }

    /** Спека ответа: проверка статус-кода живёт в шагах, а не в тестах. */
    public static ResponseSpecification status(int expectedStatus) {
        return new ResponseSpecBuilder()
                .expectStatusCode(expectedStatus)
                .build();
    }

    private static RequestSpecBuilder baseBuilder() {
        return new RequestSpecBuilder()
                .setBaseUri(Configs.API.apiBaseUrl())
                .setContentType(ContentType.JSON)
                .addHeader("x-client", Configs.API.clientHeaderSuffix())
                .addFilter(new RateLimitFilter())
                .log(LogDetail.METHOD)
                .log(LogDetail.URI);
    }
}
