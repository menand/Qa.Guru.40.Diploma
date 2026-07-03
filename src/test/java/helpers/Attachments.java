package helpers;

import config.Configs;
import io.qameta.allure.Attachment;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public final class Attachments {

    private Attachments() {
    }

    @Attachment(value = "Видео прогона BrowserStack", type = "text/html", fileExtension = ".html")
    public static String browserstackVideo(String sessionId) {
        String videoUrl = fetchVideoUrl(sessionId);
        if (videoUrl == null) {
            return "Видео недоступно для сессии " + sessionId;
        }
        return "<html><body><video width='100%' controls autoplay>"
                + "<source src='" + videoUrl + "' type='video/mp4'>"
                + "</video></body></html>";
    }

    private static String fetchVideoUrl(String sessionId) {
        // видео появляется через несколько секунд после закрытия сессии — забираем с ретраями
        for (int attempt = 0; attempt < 5; attempt++) {
            Response response = given()
                    .auth().preemptive().basic(Configs.BROWSERSTACK.user(), Configs.BROWSERSTACK.key())
                    .get("https://api-cloud.browserstack.com/app-automate/sessions/" + sessionId + ".json");
            if (response.statusCode() == 200) {
                String url = response.path("automation_session.video_url");
                if (url != null && !url.isBlank()) {
                    return url;
                }
            }
            sleep(3_000);
        }
        return null;
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
