package mobile.screens;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

/**
 * Онбординг и логин Habitica написаны на Jetpack Compose без resource-id и testTag —
 * такие элементы ищутся только по видимому тексту. Экраны после логина (классические View)
 * имеют обычные resource-id.
 */
final class MobileBy {

    private static final String APP_PACKAGE = "com.habitrpg.android.habitica";

    private MobileBy() {
    }

    static By exactText(String text) {
        return AppiumBy.androidUIAutomator("new UiSelector().text(\"" + text + "\")");
    }

    static By id(String resourceId) {
        return AppiumBy.id(APP_PACKAGE + ":id/" + resourceId);
    }
}
