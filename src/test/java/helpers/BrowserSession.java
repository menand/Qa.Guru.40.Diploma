package helpers;

import api.models.UserCredentials;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Selenide.executeJavaScript;
import static com.codeborne.selenide.Selenide.open;

/**
 * Авторизация в вебе без UI: Habitica хранит креды сессии
 * в localStorage под ключом habit-mobile-settings.
 */
public final class BrowserSession {

    private static final String SETTINGS_KEY = "habit-mobile-settings";

    private BrowserSession() {
    }

    @Step("Авторизоваться в браузере через localStorage")
    public static void authorize(UserCredentials user) {
        // лёгкая страница того же origin — только чтобы засеять localStorage
        open("/robots.txt");
        executeJavaScript(
                "localStorage.setItem(arguments[0], "
                        + "JSON.stringify({auth: {apiId: arguments[1], apiToken: arguments[2]}}))",
                SETTINGS_KEY, user.getId(), user.getApiToken());
        open("/");
    }

    /** Появились ли креды сессии в localStorage (т.е. регистрация/логин прошли). */
    public static boolean hasSession() {
        Object settings = executeJavaScript("return localStorage.getItem(arguments[0])", SETTINGS_KEY);
        return settings != null;
    }

    @Step("Прочитать учётные данные текущей web-сессии из localStorage")
    public static UserCredentials currentUser(String username, String password) {
        String apiId = executeJavaScript(
                "return JSON.parse(localStorage.getItem(arguments[0])).auth.apiId", SETTINGS_KEY);
        String apiToken = executeJavaScript(
                "return JSON.parse(localStorage.getItem(arguments[0])).auth.apiToken", SETTINGS_KEY);
        return UserCredentials.builder()
                .id(apiId)
                .apiToken(apiToken)
                .username(username)
                .password(password)
                .build();
    }
}
