package mobile.screens;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

/**
 * Тесты идут против приложения Habitica 4.4 (официальный apk из GitHub-релиза):
 * классические View-экраны с resource-id.
 */
final class MobileBy {

    private static final String APP_PACKAGE = "com.habitrpg.android.habitica";

    private MobileBy() {
    }

    static By id(String resourceId) {
        return AppiumBy.id(APP_PACKAGE + ":id/" + resourceId);
    }
}
