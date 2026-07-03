package api.specs;

import api.models.UserCredentials;
import config.Configs;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public final class ApiSpecs {

    private ApiSpecs() {
    }

    /** Спека для неавторизованных запросов (регистрация, логин, негативные проверки). */
    public static RequestSpecification anonSpec() {
        return baseBuilder()
                .addFilter(new AllureRestAssured())
                .build();
    }

    /** Спека авторизованного пользователя: заголовки x-api-user / x-api-key. */
    public static RequestSpecification authSpec(UserCredentials user) {
        return baseBuilder()
                .addFilter(new AllureRestAssured())
                .addHeader("x-api-user", user.getId())
                .addHeader("x-api-key", user.getApiToken())
                .build();
    }

    /** Спека без Allure-вложений — для сервисных вызовов вне жизненного цикла тестов (shutdown hook). */
    public static RequestSpecification quietAuthSpec(UserCredentials user) {
        return baseBuilder()
                .addHeader("x-api-user", user.getId())
                .addHeader("x-api-key", user.getApiToken())
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
